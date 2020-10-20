import {html} from 'lit-element';
import {css} from 'lit-element';
import {BaseElement} from '/webcomponents/baseelement.js'

// Tag name 'jadex-modeltree'
class ModelTree extends BaseElement 
{
	constructor()
	{
		super();
		
		this.models = []; // available component models [filename, classname]
		this.reversed = false;
		//this.myservice = "jadex.tools.web.starter.IJCCStarterService";
		this.treeid = "modeltree";
	}
	
	init()
	{
		var self = this;
		super.init().then(()=>
		{
			//console.log("modeltree");
			this.loadJSTree().then(function()
			{
				//console.log("jstree");
				
				// init tree
				$(function() 
				{ 
					self.getTree(self.treeid).jstree(
					{
						"core" : {"check_callback" : true},
						"plugins" : ["sort"],
						'sort': function(a, b) 
						{
					        var a1 = this.get_node(a);
					        var b1 = this.get_node(b);
					        if(a1.icon == b1.icon)
					        {
					            return (a1.text > b1.text) ? 1 : -1;
					        } 
					        else 
					        {
					            return (a1.icon > b1.icon) ? 1 : -1;
					        }
						}
					});
					
					// no args here
					//console.log("getComponentModels start");
					axios.get(self.getMethodPrefix()+'&methodname=getComponentModels', self.transform).then(function(resp)
					{
						//console.log("getComponentModels"+resp.data);
						
						self.models = resp.data;
						
						self.createModelTree(self.treeid);
						//$('#'+treeid).jstree('open_all');
						var childs = self.getTree(self.treeid).jstree('get_node', '#').children;
						for(var i=0; i<childs.length; i++)
						{
							self.getTree(self.treeid).jstree("open_node", childs[i]);
						}
						
						//console.log("models loaded");
						
						//$("#"+treeid).jstree("open_node", '#');
						self.requestUpdate();
						
						self.getTree(self.treeid).on('select_node.jstree', function (e, data) 
						{
							self.select(data.instance.get_path(data.node, '.'));
						});
					});
				});
			});
		})
		.catch((err)=>console.log(err));
	}
	
	loadJSTree()
	{
		var self = this;
		
		return new Promise(function(resolve, reject) 
		{
			var res1 ="jadex/tools/web/starter/libs/jstree_3.3.7.css";
			var res2 = "jadex/tools/web/starter/libs/jstree_3.3.7.js";
			var ures1 = self.getMethodPrefix()+'&methodname=loadResource&args_0='+res1+"&argtypes_0=java.lang.String";
			var ures2 = self.getMethodPrefix()+'&methodname=loadResource&args_0='+res2+"&argtypes_0=java.lang.String";
	
			//console.log("jstree load files start");
			
			var p1 = self.loadStyle(ures1);
			var p2 = self.loadScript(ures2);
			
			Promise.all([p1, p2]).then((values) => 
			{
				//console.log("js tree load files ok");
				resolve();
			})
			.catch(err => 
			{
				//console.log("js tree load files err: "+err);
				reject(err);
			});
		});
	}
	
	getMethodPrefix() 
	{
		return 'webjcc/invokeServiceMethod?cid='+this.cid+'&servicetype=jadex.tools.web.starter.IJCCStarterService';
	}
		
	getModelNames()
	{
		var ret = [];
		if(this.models.length>0)
		{
			for(var i=0; i<this.models.length; i++)
			{
				ret.push(this.getModelName(this.models[i][1]));
			}
		}
		return ret;
	}
	
	getModelName(name)
	{
		var ret = null;
		var n = name.lastIndexOf(".");
		if(n>=0)
		{
			ret = {name: name.substring(n+1), pck: name.substring(0,n)};
		}
		else
		{
			ret = {name: name, pck: null};
		}
		return ret;
	}
		
	selectModel(filename)
	{
		var self = this;
		
		//console.log("selected: "+filename);
		
		axios.get(this.getMethodPrefix()+'&methodname=loadComponentModel&args_0='+filename+"&argtypes_0=java.lang.String", this.transform).then(function(resp)
		{
			//console.log("selected model is: "+resp.data);
			
			var event = new CustomEvent("jadex-model-selected", 
			{ 
				detail: {model: resp.data},
	            bubbles: true, 
	            composed: true 
	        });
	        self.dispatchEvent(event);
			
	        //self.requestUpdate();
		});
	}
		
	select(name)
	{
		var sel;
		// called from input box
		if(typeof name!="string")
		{
			sel = this.shadowRoot.getElementById("model").value;
		}
		// called from tree
		else
		{
			var m = this.getModelName(name);
			sel = m.name+" ["+m.pck+"]";
		}
		var opts = this.shadowRoot.getElementById("models").options;
		var idx = -1;

		for(var i=0; i<opts.length; i++)
		{
			if(opts[i].value==sel)
			{
				idx = i;
				break;
			}
		}
		//console.log(idx);
		
		if(idx>-1)
		{
			var filename = this.models[idx][0];
			this.selectModel(filename);
		}
	}
		
	getTree(treeid)
	{
		return $("#"+treeid, this.shadowRoot);
	}
		
	createModelTree(treeid)
	{
		this.empty(treeid);
		
		for(var i=0; i<this.models.length; i++)
		{
			//console.log(self.models[i]);
			this.createNodes(treeid, this.models[i][1]);
		}
	}
		
	empty(treeid)
	{
		// $('#'+treeid).empty(); has problem when readding nodes :-(
		
		var roots = this.getTree(treeid).jstree().get_node('#').children;
		for(var i=0; i<roots.length; i++)
		{
			this.getTree(treeid).jstree('delete_node', roots[i]);
		}
	}
		
	createNodes(treeid, model)
	{
		var sep = ".";
		//var sep = "/";
		//if(model.indexOf("\\")!=-1)
		//	sep = "\\";
		var parts = model.split(sep);
		
		var lastprefix = '';
		var prefix = parts[0];
		
		for(var i=0; i<parts.length; i++)
		{
			prefix = !lastprefix? parts[i]: lastprefix+sep+parts[i];
			if(!this.getTree(treeid).jstree('get_node', prefix))
				this.createNode(treeid, lastprefix, prefix, parts[i], 'last');
			//else
			//	console.log("not creating: "+prefix);
			lastprefix = prefix;
		}
	}
		
	// createNode(parent, id, text, position), position 'first' or 'last'
	createNode(treeid, parent_node_id, new_node_id, new_node_text, position)//, donefunc) 
	{
		//console.log("parent="+parent_node_id+" child="+new_node_id+" childtext="+new_node_text);
		this.getTree(treeid).jstree('create_node', '#'+parent_node_id, {"text": new_node_text, "id": new_node_id }, 'last');	
	}
		
	static get styles() {
	    return css`
	    	/* Navbar styling. */
	    	/* background color. */
	    	.navbar-custom {
	    		background-color: #aaaaaa;
	    	}
	    	/* brand and text color */
	    	.navbar-custom .navbar-brand,
	    	.navbar-custom .navbar-text {
	    		color: rgba(255,255,255,.8);
	    	}
	    	/* link color */
	    	.navbar-custom .navbar-nav .nav-link {
	    		color: rgba(255,255,255,.5);
	    	}
	    	/* color of active or hovered links */
	    	.navbar-custom .nav-item.active .nav-link,
	    	.navbar-custom .nav-item:focus .nav-link,
	    	.navbar-custom .nav-item:hover .nav-link {
	    		color: #ffffff;
	    	}
	    	
	    	.w100 {
				width: 100%;
			}
			.loader {
				border: 8px solid #f3f3f3;
				border-top: 8px solid #070707; 
				border-radius: 50%;
				width: 60px;
				height: 60px;
				animation: spin 2s linear infinite;
			}
			@keyframes spin {
	  			0% { transform: rotate(0deg); }
	  			100% { transform: rotate(360deg); }
			}
	    `;
	}
	
	render() {
		return html`
			<div class="container-fluid m-0 p-0">
				<div class="row m-0 p-0">
					<div class="col-12 m-0 p-0">
						<input id="model" list="models" placeholder="Search models..." class="w100" type="text" @change="${(e) => this.select(e)}"></input>
						<datalist id="models">
							${this.getModelNames().map((model) => html`<option class="w100" value="${model.name+' ['+model.pck+']'}"></option>`)}
						</datalist>
					</div>
					<div class="col-12 m-0 p-0">
						<div id="modeltree"></div> <!-- class="scroll" -->
					</div>
				</div>
				
				${this.models.length==0? html`
				<div class="row m-0 p-0">
					<div class="col-12 m-0 p-0">
				 		<div class="loader"></div> 
				 	</div>
				</div>
				`: ''}
			</div>
		`;
	}
}

if(customElements.get('jadex-modeltree') === undefined)
	customElements.define('jadex-modeltree', ModelTree);

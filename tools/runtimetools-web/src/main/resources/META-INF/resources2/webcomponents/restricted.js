import { LitElement, html, css } from 'lit-element';
import { BaseElement } from 'base-element';

// Defined as <jadex-restricted> tag
class RestrictedElement extends BaseElement 
{
	render() 
	{
		return html`
			<div class="jumbotron jumbotron-fluid m-3 p-3">
				<div class="row">
					<div class="col-12" class="${language.getLanguage()? 'visible': 'hidden'}">
						This plugin is restricted and can used be used when logged in.
					</div>
					<div class="col-12" class="${!language.getLanguage()? 'visible': 'hidden'}">
						Dieses plugin kann nur genutzt werden, wenn man eingeloggt ist.
					</div>
				</div>
			</div>
		`
	}
}

if(customElements.get('jadex-restricted') === undefined)
	customElements.define('jadex-restricted', RestrictedElement);
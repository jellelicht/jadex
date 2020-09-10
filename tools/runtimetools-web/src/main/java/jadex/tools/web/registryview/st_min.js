/**
 * Minified by jsDelivr using Terser v3.14.1.
 * Original file: /npm/simple-datatables@2.1.13/dist/umd/simple-datatables.js
 *
 * Do NOT use SRI with dynamically generated files! More information: https://www.jsdelivr.com/using-sri-with-dynamic-files
 */
!function(t){if("object"==typeof exports&&"undefined"!=typeof module)module.exports=t();else if("function"==typeof define&&define.amd)define([],t);else{("undefined"!=typeof window?window:"undefined"!=typeof global?global:"undefined"!=typeof self?self:this).simpleDatatables=t()}}(function(){return function(){return function t(e,s,i){function a(r,h){if(!s[r]){if(!e[r]){var o="function"==typeof require&&require;if(!h&&o)return o(r,!0);if(n)return n(r,!0);var l=new Error("Cannot find module '"+r+"'");throw l.code="MODULE_NOT_FOUND",l}var d=s[r]={exports:{}};e[r][0].call(d.exports,function(t){return a(e[r][1][t]||t)},d,d.exports,t,e,s,i)}return s[r].exports}for(var n="function"==typeof require&&require,r=0;r<i.length;r++)a(i[r]);return a}}()({1:[function(t,e,s){(function(t){"use strict";"undefined"!=typeof globalThis?globalThis:"undefined"!=typeof window?window:void 0!==t||"undefined"!=typeof self&&self;function e(t,e){return t(e={exports:{}},e.exports),e.exports}var i=e(function(t,e){t.exports=function(){var t="millisecond",e="second",s="minute",i="hour",a="day",n="week",r="month",h="quarter",o="year",l=/^(\d{4})-?(\d{1,2})-?(\d{0,2})[^0-9]*(\d{1,2})?:?(\d{1,2})?:?(\d{1,2})?.?(\d{1,3})?$/,d=/\[([^\]]+)]|Y{2,4}|M{1,4}|D{1,2}|d{1,4}|H{1,2}|h{1,2}|a|A|m{1,2}|s{1,2}|Z{1,2}|SSS/g,c=function(t,e,s){var i=String(t);return!i||i.length>=e?t:""+Array(e+1-i.length).join(s)+t},u={s:c,z:function(t){var e=-t.utcOffset(),s=Math.abs(e),i=Math.floor(s/60),a=s%60;return(e<=0?"+":"-")+c(i,2,"0")+":"+c(a,2,"0")},m:function(t,e){var s=12*(e.year()-t.year())+(e.month()-t.month()),i=t.clone().add(s,r),a=e-i<0,n=t.clone().add(s+(a?-1:1),r);return Number(-(s+(e-i)/(a?i-n:n-i))||0)},a:function(t){return t<0?Math.ceil(t)||0:Math.floor(t)},p:function(l){return{M:r,y:o,w:n,d:a,h:i,m:s,s:e,ms:t,Q:h}[l]||String(l||"").toLowerCase().replace(/s$/,"")},u:function(t){return void 0===t}},p={name:"en",weekdays:"Sunday_Monday_Tuesday_Wednesday_Thursday_Friday_Saturday".split("_"),months:"January_February_March_April_May_June_July_August_September_October_November_December".split("_")},f="en",g={};g[f]=p;var m=function(t){return t instanceof w},b=function(t,e,s){var i;if(!t)return f;if("string"==typeof t)g[t]&&(i=t),e&&(g[t]=e,i=t);else{var a=t.name;g[a]=t,i=a}return s||(f=i),i},y=function(t,e,s){if(m(t))return t.clone();var i=e?"string"==typeof e?{format:e,pl:s}:e:{};return i.date=t,new w(i)},v=u;v.l=b,v.i=m,v.w=function(t,e){return y(t,{locale:e.$L,utc:e.$u})};var w=function(){function c(t){this.$L=this.$L||b(t.locale,null,!0),this.parse(t)}var u=c.prototype;return u.parse=function(t){this.$d=function(t){var e=t.date,s=t.utc;if(null===e)return new Date(NaN);if(v.u(e))return new Date;if(e instanceof Date)return new Date(e);if("string"==typeof e&&!/Z$/i.test(e)){var i=e.match(l);if(i)return s?new Date(Date.UTC(i[1],i[2]-1,i[3]||1,i[4]||0,i[5]||0,i[6]||0,i[7]||0)):new Date(i[1],i[2]-1,i[3]||1,i[4]||0,i[5]||0,i[6]||0,i[7]||0)}return new Date(e)}(t),this.init()},u.init=function(){var t=this.$d;this.$y=t.getFullYear(),this.$M=t.getMonth(),this.$D=t.getDate(),this.$W=t.getDay(),this.$H=t.getHours(),this.$m=t.getMinutes(),this.$s=t.getSeconds(),this.$ms=t.getMilliseconds()},u.$utils=function(){return v},u.isValid=function(){return!("Invalid Date"===this.$d.toString())},u.isSame=function(t,e){var s=y(t);return this.startOf(e)<=s&&s<=this.endOf(e)},u.isAfter=function(t,e){return y(t)<this.startOf(e)},u.isBefore=function(t,e){return this.endOf(e)<y(t)},u.$g=function(t,e,s){return v.u(t)?this[e]:this.set(s,t)},u.year=function(t){return this.$g(t,"$y",o)},u.month=function(t){return this.$g(t,"$M",r)},u.day=function(t){return this.$g(t,"$W",a)},u.date=function(t){return this.$g(t,"$D","date")},u.hour=function(t){return this.$g(t,"$H",i)},u.minute=function(t){return this.$g(t,"$m",s)},u.second=function(t){return this.$g(t,"$s",e)},u.millisecond=function(e){return this.$g(e,"$ms",t)},u.unix=function(){return Math.floor(this.valueOf()/1e3)},u.valueOf=function(){return this.$d.getTime()},u.startOf=function(t,h){var l=this,d=!!v.u(h)||h,c=v.p(t),u=function(t,e){var s=v.w(l.$u?Date.UTC(l.$y,e,t):new Date(l.$y,e,t),l);return d?s:s.endOf(a)},p=function(t,e){return v.w(l.toDate()[t].apply(l.toDate(),(d?[0,0,0,0]:[23,59,59,999]).slice(e)),l)},f=this.$W,g=this.$M,m=this.$D,b="set"+(this.$u?"UTC":"");switch(c){case o:return d?u(1,0):u(31,11);case r:return d?u(1,g):u(0,g+1);case n:var y=this.$locale().weekStart||0,w=(f<y?f+7:f)-y;return u(d?m-w:m+(6-w),g);case a:case"date":return p(b+"Hours",0);case i:return p(b+"Minutes",1);case s:return p(b+"Seconds",2);case e:return p(b+"Milliseconds",3);default:return this.clone()}},u.endOf=function(t){return this.startOf(t,!1)},u.$set=function(n,h){var l,d=v.p(n),c="set"+(this.$u?"UTC":""),u=(l={},l[a]=c+"Date",l.date=c+"Date",l[r]=c+"Month",l[o]=c+"FullYear",l[i]=c+"Hours",l[s]=c+"Minutes",l[e]=c+"Seconds",l[t]=c+"Milliseconds",l)[d],p=d===a?this.$D+(h-this.$W):h;if(d===r||d===o){var f=this.clone().set("date",1);f.$d[u](p),f.init(),this.$d=f.set("date",Math.min(this.$D,f.daysInMonth())).toDate()}else u&&this.$d[u](p);return this.init(),this},u.set=function(t,e){return this.clone().$set(t,e)},u.get=function(t){return this[v.p(t)]()},u.add=function(t,h){var l,d=this;t=Number(t);var c=v.p(h),u=function(e){var s=y(d);return v.w(s.date(s.date()+Math.round(e*t)),d)};if(c===r)return this.set(r,this.$M+t);if(c===o)return this.set(o,this.$y+t);if(c===a)return u(1);if(c===n)return u(7);var p=(l={},l[s]=6e4,l[i]=36e5,l[e]=1e3,l)[c]||1,f=this.valueOf()+t*p;return v.w(f,this)},u.subtract=function(t,e){return this.add(-1*t,e)},u.format=function(t){var e=this;if(!this.isValid())return"Invalid Date";var s=t||"YYYY-MM-DDTHH:mm:ssZ",i=v.z(this),a=this.$locale(),n=this.$H,r=this.$m,h=this.$M,o=a.weekdays,l=a.months,c=function(t,i,a,n){return t&&(t[i]||t(e,s))||a[i].substr(0,n)},u=function(t){return v.s(n%12||12,t,"0")},p=a.meridiem||function(t,e,s){var i=t<12?"AM":"PM";return s?i.toLowerCase():i},f={YY:String(this.$y).slice(-2),YYYY:this.$y,M:h+1,MM:v.s(h+1,2,"0"),MMM:c(a.monthsShort,h,l,3),MMMM:l[h]||l(this,s),D:this.$D,DD:v.s(this.$D,2,"0"),d:String(this.$W),dd:c(a.weekdaysMin,this.$W,o,2),ddd:c(a.weekdaysShort,this.$W,o,3),dddd:o[this.$W],H:String(n),HH:v.s(n,2,"0"),h:u(1),hh:u(2),a:p(n,r,!0),A:p(n,r,!1),m:String(r),mm:v.s(r,2,"0"),s:String(this.$s),ss:v.s(this.$s,2,"0"),SSS:v.s(this.$ms,3,"0"),Z:i};return s.replace(d,function(t,e){return e||f[t]||i.replace(":","")})},u.utcOffset=function(){return 15*-Math.round(this.$d.getTimezoneOffset()/15)},u.diff=function(t,l,d){var c,u=v.p(l),p=y(t),f=6e4*(p.utcOffset()-this.utcOffset()),g=this-p,m=v.m(this,p);return m=(c={},c[o]=m/12,c[r]=m,c[h]=m/3,c[n]=(g-f)/6048e5,c[a]=(g-f)/864e5,c[i]=g/36e5,c[s]=g/6e4,c[e]=g/1e3,c)[u]||g,d?m:v.a(m)},u.daysInMonth=function(){return this.endOf(r).$D},u.$locale=function(){return g[this.$L]},u.locale=function(t,e){if(!t)return this.$L;var s=this.clone();return s.$L=b(t,e,!0),s},u.clone=function(){return v.w(this.toDate(),this)},u.toDate=function(){return new Date(this.$d)},u.toJSON=function(){return this.isValid()?this.toISOString():null},u.toISOString=function(){return this.$d.toISOString()},u.toString=function(){return this.$d.toUTCString()},c}();return y.prototype=w.prototype,y.extend=function(t,e){return t(e,w,y),y},y.locale=b,y.isDayjs=m,y.unix=function(t){return y(1e3*t)},y.en=g[f],y.Ls=g,y}()}),a=e(function(t,e){var s,i,a,n,r,h,o,l,d;t.exports=(i=/(\[[^[]*\])|([-:\/.()\s]+)|(A|a|YYYY|YY?|MM?M?M?|Do|DD?|hh?|HH?|mm?|ss?|S{1,3}|z|ZZ?)/g,r=/\d*[^\s\d-:\/.()]+/,o=[/[+-]\d\d:?\d\d/,function(t){var e,s;(this.zone||(this.zone={})).offset=0==(s=60*(e=t.match(/([+-]|\d\d)/g))[1]+ +e[2])?0:"+"===e[0]?-s:s}],l={A:[/[AP]M/,function(t){this.afternoon="PM"===t}],a:[/[ap]m/,function(t){this.afternoon="pm"===t}],S:[/\d/,function(t){this.milliseconds=100*+t}],SS:[a=/\d\d/,function(t){this.milliseconds=10*+t}],SSS:[/\d{3}/,function(t){this.milliseconds=+t}],s:[n=/\d\d?/,(h=function(t){return function(e){this[t]=+e}})("seconds")],ss:[n,h("seconds")],m:[n,h("minutes")],mm:[n,h("minutes")],H:[n,h("hours")],h:[n,h("hours")],HH:[n,h("hours")],hh:[n,h("hours")],D:[n,h("day")],DD:[a,h("day")],Do:[r,function(t){var e=s.ordinal,i=t.match(/\d+/);if(this.day=i[0],e)for(var a=1;a<=31;a+=1)e(a).replace(/\[|\]/g,"")===t&&(this.day=a)}],M:[n,h("month")],MM:[a,h("month")],MMM:[r,function(t){var e=s,i=e.months,a=e.monthsShort,n=a?a.findIndex(function(e){return e===t}):i.findIndex(function(e){return e.substr(0,3)===t});if(n<0)throw new Error;this.month=n+1}],MMMM:[r,function(t){var e=s.months.indexOf(t);if(e<0)throw new Error;this.month=e+1}],Y:[/[+-]?\d+/,h("year")],YY:[a,function(t){t=+t,this.year=t+(t>68?1900:2e3)}],YYYY:[/\d{4}/,h("year")],Z:o,ZZ:o},d=function(t,e,s){try{var a=function(t){for(var e=t.match(i),s=e.length,a=0;a<s;a+=1){var n=e[a],r=l[n],h=r&&r[0],o=r&&r[1];e[a]=o?{regex:h,parser:o}:n.replace(/^\[|\]$/g,"")}return function(t){for(var i={},a=0,n=0;a<s;a+=1){var r=e[a];if("string"==typeof r)n+=r.length;else{var h=r.regex,o=r.parser,l=t.substr(n),d=h.exec(l)[0];o.call(i,d),t=t.replace(d,"")}}return function(t){var e=t.afternoon;if(void 0!==e){var s=t.hours;e?s<12&&(t.hours+=12):12===s&&(t.hours=0),delete t.afternoon}}(i),i}}(e)(t),n=a.year,r=a.month,h=a.day,o=a.hours,d=a.minutes,c=a.seconds,u=a.milliseconds,p=a.zone;if(p)return new Date(Date.UTC(n,r-1,h,o||0,d||0,c||0,u||0)+60*p.offset*1e3);var f=new Date,g=n||f.getFullYear(),m=r>0?r-1:f.getMonth(),b=h||f.getDate(),y=o||0,v=d||0,w=c||0,C=u||0;return s?new Date(Date.UTC(g,m,b,y,v,w,C)):new Date(g,m,b,y,v,w,C)}catch(t){return new Date("")}},function(t,e,i){var a=e.prototype,n=a.parse;a.parse=function(t){var e=t.date,a=t.format,r=t.pl,h=t.utc;this.$u=h,a?(s=r?i.Ls[r]:this.$locale(),this.$d=d(e,a,h),this.init(t)):n.call(this,t)}})});i.extend(a);s.parseDate=((t,e)=>{let s=!1;if(e)switch(e){case"ISO_8601":s=t;break;case"RFC_2822":s=i(t,"ddd, MM MMM YYYY HH:mm:ss ZZ").format("YYYYMMDD");break;case"MYSQL":s=i(t,"YYYY-MM-DD hh:mm:ss").format("YYYYMMDD");break;case"UNIX":s=i(t).unix();break;default:s=i(t,e).format("YYYYMMDD")}return s})}).call(this,"undefined"!=typeof global?global:"undefined"!=typeof self?self:"undefined"!=typeof window?window:{})},{}],2:[function(t,e,s){"use strict";Object.defineProperty(s,"__esModule",{value:!0});const i=t=>"[object Object]"===Object.prototype.toString.call(t),a=t=>{let e=!1;try{e=JSON.parse(t)}catch(t){return!1}return!(null===e||!Array.isArray(e)&&!i(e))&&e},n=(t,e)=>{const s=document.createElement(t);if(e&&"object"==typeof e)for(const t in e)"html"===t?s.innerHTML=e[t]:s.setAttribute(t,e[t]);return s},r=t=>{t instanceof NodeList?t.forEach(t=>r(t)):t.innerHTML=""},h=(t,e,s)=>n("li",{class:t,html:`<a href="#" data-page="${e}">${s}</a>`}),o=(t,e)=>{let s,i;1===e?(s=0,i=t.length):-1===e&&(s=t.length-1,i=-1);for(let a=!0;a;){a=!1;for(let n=s;n!=i;n+=e)if(t[n+e]&&t[n].value>t[n+e].value){const s=t[n],i=t[n+e],r=s;t[n]=i,t[n+e]=r,a=!0}}return t},l=(t,e,s,i,a)=>{let r;const h=2*(i=i||2);let o=e-i,l=e+i;const d=[],c=[];e<4-i+h?l=3+h:e>s-(3-i+h)&&(o=s-(2+h));for(let e=1;e<=s;e++)if(1==e||e==s||e>=o&&e<=l){const s=t[e-1];s.classList.remove("active"),d.push(s)}return d.forEach(e=>{const s=e.children[0].getAttribute("data-page");if(r){const e=r.children[0].getAttribute("data-page");if(s-e==2)c.push(t[e]);else if(s-e!=1){const t=n("li",{class:"ellipsis",html:`<a href="#">${a}</a>`});c.push(t)}}c.push(e),r=e}),c};class d{constructor(t,e){return this.dt=t,this.rows=e,this}build(t){const e=n("tr");let s=this.dt.headings;return s.length||(s=t.map(()=>"")),s.forEach((s,i)=>{const a=n("td");t[i]&&t[i].length||(t[i]=""),a.innerHTML=t[i],a.data=t[i],e.appendChild(a)}),e}render(t){return t}add(t){if(Array.isArray(t)){const e=this.dt;Array.isArray(t[0])?t.forEach(t=>{e.data.push(this.build(t))}):e.data.push(this.build(t)),e.data.length&&(e.hasRows=!0),this.update(),e.columns().rebuild()}}remove(t){const e=this.dt;Array.isArray(t)?(t.sort((t,e)=>e-t),t.forEach(t=>{e.data.splice(t,1)})):"all"==t?e.data=[]:e.data.splice(t,1),e.data.length||(e.hasRows=!1),this.update(),e.columns().rebuild()}update(){this.dt.data.forEach((t,e)=>{t.dataIndex=e})}}class c{constructor(t){return this.dt=t,this}swap(t){if(t.length&&2===t.length){const e=[];this.dt.headings.forEach((t,s)=>{e.push(s)});const s=t[0],i=t[1],a=e[i];e[i]=e[s],e[s]=a,this.order(e)}}order(t){let e,s,i,a,n,r,h;const o=[[],[],[],[]],l=this.dt;t.forEach((t,i)=>{n=l.headings[t],r="false"!==n.getAttribute("data-sortable"),(e=n.cloneNode(!0)).originalCellIndex=i,e.sortable=r,o[0].push(e),l.hiddenColumns.includes(t)||((s=n.cloneNode(!0)).originalCellIndex=i,s.sortable=r,o[1].push(s))}),l.data.forEach((e,s)=>{i=e.cloneNode(!1),a=e.cloneNode(!1),i.dataIndex=a.dataIndex=s,null!==e.searchIndex&&void 0!==e.searchIndex&&(i.searchIndex=a.searchIndex=e.searchIndex),t.forEach(t=>{(h=e.cells[t].cloneNode(!0)).data=e.cells[t].data,i.appendChild(h),l.hiddenColumns.includes(t)||((h=e.cells[t].cloneNode(!0)).data=e.cells[t].data,a.appendChild(h))}),o[2].push(i),o[3].push(a)}),l.headings=o[0],l.activeHeadings=o[1],l.data=o[2],l.activeRows=o[3],l.update()}hide(t){if(t.length){const e=this.dt;t.forEach(t=>{e.hiddenColumns.includes(t)||e.hiddenColumns.push(t)}),this.rebuild()}}show(t){if(t.length){let e;const s=this.dt;t.forEach(t=>{(e=s.hiddenColumns.indexOf(t))>-1&&s.hiddenColumns.splice(e,1)}),this.rebuild()}}visible(t){let e;const s=this.dt;return t=t||s.headings.map(t=>t.originalCellIndex),isNaN(t)?Array.isArray(t)&&(e=[],t.forEach(t=>{e.push(!s.hiddenColumns.includes(t))})):e=!s.hiddenColumns.includes(t),e}add(t){let e;const s=document.createElement("th");if(!this.dt.headings.length)return this.dt.insert({headings:[t.heading],data:t.data.map(t=>[t])}),void this.rebuild();this.dt.hiddenHeader?s.innerHTML="":t.heading.nodeName?s.appendChild(t.heading):s.innerHTML=t.heading,this.dt.headings.push(s),this.dt.data.forEach((s,i)=>{t.data[i]&&(e=document.createElement("td"),t.data[i].nodeName?e.appendChild(t.data[i]):e.innerHTML=t.data[i],e.data=e.innerHTML,t.render&&(e.innerHTML=t.render.call(this,e.data,e,s)),s.appendChild(e))}),t.type&&s.setAttribute("data-type",t.type),t.format&&s.setAttribute("data-format",t.format),t.hasOwnProperty("sortable")&&(s.sortable=t.sortable,s.setAttribute("data-sortable",!0===t.sortable?"true":"false")),this.rebuild(),this.dt.renderHeader()}remove(t){Array.isArray(t)?(t.sort((t,e)=>e-t),t.forEach(t=>this.remove(t))):(this.dt.headings.splice(t,1),this.dt.data.forEach(e=>{e.removeChild(e.cells[t])})),this.rebuild()}filter(t,e,s,i){const a=this.dt;if(a.filterState||(a.filterState={originalData:a.data}),!a.filterState[t]){const e=[...i,()=>!0];a.filterState[t]=function(){let t=0;return()=>e[t++%e.length]}()}const n=a.filterState[t](),r=Array.from(a.filterState.originalData).filter(e=>{const s=e.cells[t],i=s.hasAttribute("data-content")?s.getAttribute("data-content"):s.innerText;return"function"==typeof n?n(i):i===n});a.data=r,this.rebuild(),a.update(),s||a.emit("datatable.sort",t,e)}sort(e,s,i){const a=this.dt;if(a.hasHeadings&&(e<0||e>a.headings.length))return!1;const n=a.options.filters&&a.options.filters[a.headings[e].textContent];if(n&&0!==n.length)return void this.filter(e,s,i,n);a.sorting=!0,i||a.emit("datatable.sorting",e,s);let r=a.data;const h=[],l=[];let d=0,c=0;const u=a.headings[e],p=[];if("date"===u.getAttribute("data-type")){let e=!1;u.hasAttribute("data-format")&&(e=u.getAttribute("data-format")),p.push(new Promise(function(e){e(t("./date-4abbfef1.js"))}).then(({parseDate:t})=>s=>t(s,e)))}Promise.all(p).then(t=>{const n=t[0];let p,f;Array.from(r).forEach(t=>{const s=t.cells[e],i=s.hasAttribute("data-content")?s.getAttribute("data-content"):s.innerText;let a;a=n?n(i):"string"==typeof i?i.replace(/(\$|,|\s|%)/g,""):i,parseFloat(a)==a?l[c++]={value:Number(a),row:t}:h[d++]={value:"string"==typeof i?i.toLowerCase():i,row:t}}),s||(s=u.classList.contains("asc")?"desc":"asc"),"desc"==s?(p=o(h,-1),f=o(l,-1),u.classList.remove("asc"),u.classList.add("desc")):(p=o(l,1),f=o(h,1),u.classList.remove("desc"),u.classList.add("asc")),a.lastTh&&u!=a.lastTh&&(a.lastTh.classList.remove("desc"),a.lastTh.classList.remove("asc")),a.lastTh=u,r=p.concat(f),a.data=[];const g=[];r.forEach((t,e)=>{a.data.push(t.row),null!==t.row.searchIndex&&void 0!==t.row.searchIndex&&g.push(e)}),a.searchData=g,this.rebuild(),a.update(),i||a.emit("datatable.sort",e,s)})}rebuild(){let t,e,s,i;const a=this.dt,n=[];a.activeRows=[],a.activeHeadings=[],a.headings.forEach((t,e)=>{t.originalCellIndex=e,t.sortable="false"!==t.getAttribute("data-sortable"),a.hiddenColumns.includes(e)||a.activeHeadings.push(t)}),a.data.forEach((r,h)=>{t=r.cloneNode(!1),e=r.cloneNode(!1),t.dataIndex=e.dataIndex=h,null!==r.searchIndex&&void 0!==r.searchIndex&&(t.searchIndex=e.searchIndex=r.searchIndex),Array.from(r.cells).forEach(n=>{(s=n.cloneNode(!0)).data=n.data,t.appendChild(s),a.hiddenColumns.includes(s.cellIndex)||((i=s.cloneNode(!0)).data=s.data,e.appendChild(i))}),n.push(t),a.activeRows.push(e)}),a.data=n,a.update()}}const u=function(t){let e=!1,s=!1;if((t=t||this.options.data).headings){e=n("thead");const s=n("tr");t.headings.forEach(t=>{const e=n("th",{html:t});s.appendChild(e)}),e.appendChild(s)}t.data&&t.data.length&&(s=n("tbody"),t.data.forEach(e=>{if(t.headings&&t.headings.length!==e.length)throw new Error("The number of rows do not match the number of headings.");const i=n("tr");e.forEach(t=>{const e=n("td",{html:t});i.appendChild(e)}),s.appendChild(i)})),e&&(null!==this.table.tHead&&this.table.removeChild(this.table.tHead),this.table.appendChild(e)),s&&(this.table.tBodies.length&&this.table.removeChild(this.table.tBodies[0]),this.table.appendChild(s))},p={sortable:!0,searchable:!0,paging:!0,perPage:10,perPageSelect:[5,10,15,20,25],nextPrev:!0,firstLast:!1,prevText:"&lsaquo;",nextText:"&rsaquo;",firstText:"&laquo;",lastText:"&raquo;",ellipsisText:"&hellip;",ascText:"▴",descText:"▾",truncatePager:!0,pagerDelta:2,scrollY:"",fixedColumns:!0,fixedHeight:!1,header:!0,footer:!1,labels:{placeholder:"Search...",perPage:"{select} entries per page",noRows:"No entries found",info:"Showing {start} to {end} of {rows} entries"},layout:{top:"{select}{search}",bottom:"{info}{pager}"}};class f{constructor(t,e={}){if(this.initialized=!1,this.options={...p,...e,layout:{...p.layout,...e.layout},labels:{...p.labels,...e.labels}},"string"==typeof t&&(t=document.querySelector(t)),this.initialLayout=t.innerHTML,this.initialSortable=this.options.sortable,this.options.header||(this.options.sortable=!1),null===t.tHead&&(!this.options.data||this.options.data&&!this.options.data.headings)&&(this.options.sortable=!1),t.tBodies.length&&!t.tBodies[0].rows.length&&this.options.data&&!this.options.data.data)throw new Error("You seem to be using the data option, but you've not defined any rows.");this.table=t,this.init()}static extend(t,e){"function"==typeof e?f.prototype[t]=e:f[t]=e}init(t){if(this.initialized||this.table.classList.contains("dataTable-table"))return!1;Object.assign(this.options,t||{}),this.currentPage=1,this.onFirstPage=!0,this.hiddenColumns=[],this.columnRenderers=[],this.selectedColumns=[],this.render(),setTimeout(()=>{this.emit("datatable.init"),this.initialized=!0,this.options.plugins&&Object.entries(this.options.plugins).forEach(([t,e])=>{this[t]&&"function"==typeof this[t]&&(this[t]=this[t](e,{createElement:n}),e.enabled&&this[t].init&&"function"==typeof this[t].init&&this[t].init())})},10)}render(t){if(t){switch(t){case"page":this.renderPage();break;case"pager":this.renderPager();break;case"header":this.renderHeader()}return!1}const e=this.options;let s="";if(e.data&&u.call(this),e.ajax){const t=e.ajax,s=new XMLHttpRequest,i=t=>{this.emit("datatable.ajax.progress",t,s)},a=e=>{if(4===s.readyState)if(this.emit("datatable.ajax.loaded",e,s),200===s.status){const i={};i.data=t.load?t.load.call(this,s):s.responseText,i.type="json",t.content&&t.content.type&&(i.type=t.content.type,Object.assign(i,t.content)),this.import(i),this.setColumns(!0),this.emit("datatable.ajax.success",e,s)}else this.emit("datatable.ajax.error",e,s)},n=t=>{this.emit("datatable.ajax.error",t,s)},r=t=>{this.emit("datatable.ajax.abort",t,s)};s.addEventListener("progress",i,!1),s.addEventListener("load",a,!1),s.addEventListener("error",n,!1),s.addEventListener("abort",r,!1),this.emit("datatable.ajax.loading",s),s.open("GET","string"==typeof t?e.ajax:e.ajax.url),s.send()}if(this.body=this.table.tBodies[0],this.head=this.table.tHead,this.foot=this.table.tFoot,this.body||(this.body=n("tbody"),this.table.appendChild(this.body)),this.hasRows=this.body.rows.length>0,!this.head){const t=n("thead"),s=n("tr");this.hasRows&&(Array.from(this.body.rows[0].cells).forEach(()=>{s.appendChild(n("th"))}),t.appendChild(s)),this.head=t,this.table.insertBefore(this.head,this.body),this.hiddenHeader=!e.ajax}if(this.headings=[],this.hasHeadings=this.head.rows.length>0,this.hasHeadings&&(this.header=this.head.rows[0],this.headings=[].slice.call(this.header.cells)),e.header||this.head&&this.table.removeChild(this.table.tHead),e.footer?this.head&&!this.foot&&(this.foot=n("tfoot",{html:this.head.innerHTML}),this.table.appendChild(this.foot)):this.foot&&this.table.removeChild(this.table.tFoot),this.wrapper=n("div",{class:"dataTable-wrapper dataTable-loading"}),s+="<div class='dataTable-top'>",s+=e.layout.top,s+="</div>",e.scrollY.length?s+=`<div class='dataTable-container' style='height: ${e.scrollY}; overflow-Y: auto;'></div>`:s+="<div class='dataTable-container'></div>",s+="<div class='dataTable-bottom'>",s+=e.layout.bottom,s=(s+="</div>").replace("{info}",e.paging?"<div class='dataTable-info'></div>":""),e.paging&&e.perPageSelect){let t="<div class='dataTable-dropdown'><label>";t+=e.labels.perPage,t+="</label></div>";const i=n("select",{class:"dataTable-selector"});e.perPageSelect.forEach(t=>{const s=t===e.perPage,a=new Option(t,t,s,s);i.add(a)}),t=t.replace("{select}",i.outerHTML),s=s.replace("{select}",t)}else s=s.replace("{select}","");if(e.searchable){const t=`<div class='dataTable-search'><input class='dataTable-input' placeholder='${e.labels.placeholder}' type='text'></div>`;s=s.replace("{search}",t)}else s=s.replace("{search}","");this.hasHeadings&&this.render("header"),this.table.classList.add("dataTable-table");const i=n("div",{class:"dataTable-pagination"}),a=n("ul");i.appendChild(a),s=s.replace(/\{pager\}/g,i.outerHTML),this.wrapper.innerHTML=s,this.container=this.wrapper.querySelector(".dataTable-container"),this.pagers=this.wrapper.querySelectorAll(".dataTable-pagination"),this.label=this.wrapper.querySelector(".dataTable-info"),this.table.parentNode.replaceChild(this.wrapper,this.table),this.container.appendChild(this.table),this.rect=this.table.getBoundingClientRect(),this.data=Array.from(this.body.rows),this.activeRows=this.data.slice(),this.activeHeadings=this.headings.slice(),this.update(),e.ajax||this.setColumns(),this.fixHeight(),this.fixColumns(),e.header||this.wrapper.classList.add("no-header"),e.footer||this.wrapper.classList.add("no-footer"),e.sortable&&this.wrapper.classList.add("sortable"),e.searchable&&this.wrapper.classList.add("searchable"),e.fixedHeight&&this.wrapper.classList.add("fixed-height"),e.fixedColumns&&this.wrapper.classList.add("fixed-columns"),this.bindEvents()}renderPage(){if(this.hasHeadings&&(r(this.header),this.activeHeadings.forEach(t=>this.header.appendChild(t))),this.hasRows&&this.totalPages){this.currentPage>this.totalPages&&(this.currentPage=1);const t=this.currentPage-1,e=document.createDocumentFragment();this.pages[t].forEach(t=>e.appendChild(this.rows().render(t))),this.clear(e),this.onFirstPage=1===this.currentPage,this.onLastPage=this.currentPage===this.lastPage}else this.setMessage(this.options.labels.noRows);let t,e=0,s=0,i=0;if(this.totalPages&&(i=(s=(e=this.currentPage-1)*this.options.perPage)+this.pages[e].length,s+=1,t=this.searching?this.searchData.length:this.data.length),this.label&&this.options.labels.info.length){const e=this.options.labels.info.replace("{start}",s).replace("{end}",i).replace("{page}",this.currentPage).replace("{pages}",this.totalPages).replace("{rows}",t);this.label.innerHTML=t?e:""}1==this.currentPage&&this.fixHeight()}renderPager(){if(r(this.pagers),this.totalPages>1){const t="pager",e=document.createDocumentFragment(),s=this.onFirstPage?1:this.currentPage-1,i=this.onLastPage?this.totalPages:this.currentPage+1;this.options.firstLast&&e.appendChild(h(t,1,this.options.firstText)),this.options.nextPrev&&e.appendChild(h(t,s,this.options.prevText));let a=this.links;this.options.truncatePager&&(a=l(this.links,this.currentPage,this.pages.length,this.options.pagerDelta,this.options.ellipsisText)),this.links[this.currentPage-1].classList.add("active"),a.forEach(t=>{t.classList.remove("active"),e.appendChild(t)}),this.links[this.currentPage-1].classList.add("active"),this.options.nextPrev&&e.appendChild(h(t,i,this.options.nextText)),this.options.firstLast&&e.appendChild(h(t,this.totalPages,this.options.lastText)),this.pagers.forEach(t=>{t.appendChild(e.cloneNode(!0))})}}renderHeader(){this.labels=[],this.headings&&this.headings.length&&this.headings.forEach((t,e)=>{if(this.labels[e]=t.textContent,t.firstElementChild&&t.firstElementChild.classList.contains("dataTable-sorter")&&(t.innerHTML=t.firstElementChild.innerHTML),t.sortable="false"!==t.getAttribute("data-sortable"),t.originalCellIndex=e,this.options.sortable&&t.sortable){const e=n("a",{href:"#",class:"dataTable-sorter",html:t.innerHTML});t.innerHTML="",t.setAttribute("data-sortable",""),t.appendChild(e)}}),this.fixColumns()}bindEvents(){const t=this.options;if(t.perPageSelect){const e=this.wrapper.querySelector(".dataTable-selector");e&&e.addEventListener("change",()=>{t.perPage=parseInt(e.value,10),this.update(),this.fixHeight(),this.emit("datatable.perpage",t.perPage)},!1)}t.searchable&&(this.input=this.wrapper.querySelector(".dataTable-input"),this.input&&this.input.addEventListener("keyup",()=>this.search(this.input.value),!1)),this.wrapper.addEventListener("click",e=>{const s=e.target;"a"===s.nodeName.toLowerCase()&&(s.hasAttribute("data-page")?(this.page(s.getAttribute("data-page")),e.preventDefault()):t.sortable&&s.classList.contains("dataTable-sorter")&&"false"!=s.parentNode.getAttribute("data-sortable")&&(this.columns().sort(this.headings.indexOf(s.parentNode)),e.preventDefault()))},!1),window.addEventListener("resize",()=>{this.rect=this.container.getBoundingClientRect(),this.fixColumns()})}setColumns(t){t||this.data.forEach(t=>{Array.from(t.cells).forEach(t=>{t.data=t.innerHTML})}),this.options.columns&&this.headings.length&&this.options.columns.forEach(t=>{Array.isArray(t.select)||(t.select=[t.select]),t.hasOwnProperty("render")&&"function"==typeof t.render&&(this.selectedColumns=this.selectedColumns.concat(t.select),this.columnRenderers.push({columns:t.select,renderer:t.render})),t.select.forEach(e=>{const s=this.headings[e];t.type&&s.setAttribute("data-type",t.type),t.format&&s.setAttribute("data-format",t.format),t.hasOwnProperty("sortable")&&s.setAttribute("data-sortable",t.sortable),t.hasOwnProperty("hidden")&&!1!==t.hidden&&this.columns().hide([e]),t.hasOwnProperty("sort")&&1===t.select.length&&this.columns().sort(t.select[0],t.sort,!0)})}),this.hasRows&&(this.data.forEach((t,e)=>{t.dataIndex=e,Array.from(t.cells).forEach(t=>{t.data=t.innerHTML})}),this.selectedColumns.length&&this.data.forEach(t=>{Array.from(t.cells).forEach((e,s)=>{this.selectedColumns.includes(s)&&this.columnRenderers.forEach(i=>{i.columns.includes(s)&&(e.innerHTML=i.renderer.call(this,e.data,e,t))})})}),this.columns().rebuild()),this.render("header")}destroy(){this.table.innerHTML=this.initialLayout,this.table.classList.remove("dataTable-table"),this.wrapper.parentNode.replaceChild(this.table,this.wrapper),this.initialized=!1}update(){this.wrapper.classList.remove("dataTable-empty"),this.paginate(this),this.render("page"),this.links=[];let t=this.pages.length;for(;t--;){const e=t+1;this.links[t]=h(0===t?"active":"",e,e)}this.sorting=!1,this.render("pager"),this.rows().update(),this.emit("datatable.update")}paginate(){const t=this.options.perPage;let e=this.activeRows;return this.searching&&(e=[],this.searchData.forEach(t=>e.push(this.activeRows[t]))),this.options.paging?this.pages=e.map((s,i)=>i%t==0?e.slice(i,i+t):null).filter(t=>t):this.pages=[e],this.totalPages=this.lastPage=this.pages.length,this.totalPages}fixColumns(){if((this.options.scrollY.length||this.options.fixedColumns)&&this.activeHeadings&&this.activeHeadings.length){let t,e=!1;if(this.columnWidths=[],this.table.tHead){if(this.options.scrollY.length&&((e=n("thead")).appendChild(n("tr")),e.style.height="0px",this.headerTable&&(this.table.tHead=this.headerTable.tHead)),this.activeHeadings.forEach(t=>{t.style.width=""}),this.activeHeadings.forEach((t,s)=>{const i=t.offsetWidth,a=i/this.rect.width*100;if(t.style.width=`${a}%`,this.columnWidths[s]=i,this.options.scrollY.length){const t=n("th");e.firstElementChild.appendChild(t),t.style.width=`${a}%`,t.style.paddingTop="0",t.style.paddingBottom="0",t.style.border="0"}}),this.options.scrollY.length){const t=this.table.parentElement;if(!this.headerTable){this.headerTable=n("table",{class:"dataTable-table"});const e=n("div",{class:"dataTable-headercontainer"});e.appendChild(this.headerTable),t.parentElement.insertBefore(e,t)}const s=this.table.tHead;this.table.replaceChild(e,s),this.headerTable.tHead=s,this.headerTable.parentElement.style.paddingRight=`${this.headerTable.clientWidth-this.table.clientWidth+parseInt(this.headerTable.parentElement.style.paddingRight||"0",10)}px`,t.scrollHeight>t.clientHeight&&(t.style.overflowY="scroll")}}else{t=[],e=n("thead");const s=n("tr");Array.from(this.table.tBodies[0].rows[0].cells).forEach(()=>{const e=n("th");s.appendChild(e),t.push(e)}),e.appendChild(s),this.table.insertBefore(e,this.body);const i=[];t.forEach((t,e)=>{const s=t.offsetWidth,a=s/this.rect.width*100;i.push(a),this.columnWidths[e]=s}),this.data.forEach(t=>{Array.from(t.cells).forEach((t,e)=>{this.columns(t.cellIndex).visible()&&(t.style.width=`${i[e]}%`)})}),this.table.removeChild(e)}}}fixHeight(){this.options.fixedHeight&&(this.container.style.height=null,this.rect=this.container.getBoundingClientRect(),this.container.style.height=`${this.rect.height}px`)}search(t){return!!this.hasRows&&(t=t.toLowerCase(),this.currentPage=1,this.searching=!0,this.searchData=[],t.length?(this.clear(),this.data.forEach((e,s)=>{const i=this.searchData.includes(e);t.split(" ").reduce((t,s)=>{let i=!1,a=null,n=null;for(let t=0;t<e.cells.length;t++)if((n=(a=e.cells[t]).hasAttribute("data-content")?a.getAttribute("data-content"):a.textContent).toLowerCase().includes(s)&&this.columns(a.cellIndex).visible()){i=!0;break}return t&&i},!0)&&!i?(e.searchIndex=s,this.searchData.push(s)):e.searchIndex=null}),this.wrapper.classList.add("search-results"),this.searchData.length?this.update():(this.wrapper.classList.remove("search-results"),this.setMessage(this.options.labels.noRows)),void this.emit("datatable.search",t,this.searchData)):(this.searching=!1,this.update(),this.emit("datatable.search",t,this.searchData),this.wrapper.classList.remove("search-results"),!1))}page(t){return t!=this.currentPage&&(isNaN(t)||(this.currentPage=parseInt(t,10)),!(t>this.pages.length||t<0)&&(this.render("page"),this.render("pager"),void this.emit("datatable.page",t)))}sortColumn(t,e){this.columns().sort(t,e)}insert(t){let e=[];if(i(t)){if(t.headings&&!this.hasHeadings&&!this.hasRows){const e=n("tr");t.headings.forEach(t=>{const s=n("th",{html:t});e.appendChild(s)}),this.head.appendChild(e),this.header=e,this.headings=[].slice.call(e.cells),this.hasHeadings=!0,this.options.sortable=this.initialSortable,this.render("header"),this.activeHeadings=this.headings.slice()}t.data&&Array.isArray(t.data)&&(e=t.data)}else Array.isArray(t)&&t.forEach(t=>{const s=[];Object.entries(t).forEach(([t,e])=>{const i=this.labels.indexOf(t);i>-1&&(s[i]=e)}),e.push(s)});e.length&&(this.rows().add(e),this.hasRows=!0),this.update(),this.setColumns(),this.fixColumns()}refresh(){this.options.searchable&&(this.input.value="",this.searching=!1),this.currentPage=1,this.onFirstPage=!0,this.update(),this.emit("datatable.refresh")}clear(t){this.body&&r(this.body);let e=this.body;this.body||(e=this.table),t&&("string"==typeof t&&(document.createDocumentFragment().innerHTML=t),e.appendChild(t))}export(t){if(!this.hasHeadings&&!this.hasRows)return!1;const e=this.activeHeadings;let s=[];const a=[];let n,r,h,o;if(!i(t))return!1;const l={download:!0,skipColumn:[],lineDelimiter:"\n",columnDelimiter:",",tableName:"myTable",replacer:null,space:4,...t};if(l.type){if("txt"!==l.type&&"csv"!==l.type||(s[0]=this.header),l.selection)if(isNaN(l.selection)){if(Array.isArray(l.selection))for(n=0;n<l.selection.length;n++)s=s.concat(this.pages[l.selection[n]-1])}else s=s.concat(this.pages[l.selection-1]);else s=s.concat(this.activeRows);if(s.length){if("txt"===l.type||"csv"===l.type){for(h="",n=0;n<s.length;n++){for(r=0;r<s[n].cells.length;r++)if(!l.skipColumn.includes(e[r].originalCellIndex)&&this.columns(e[r].originalCellIndex).visible()){let t=s[n].cells[r].textContent;(t=(t=(t=(t=(t=t.trim()).replace(/\s{2,}/g," ")).replace(/\n/g,"  ")).replace(/"/g,'""')).replace(/#/g,"%23")).includes(",")&&(t=`"${t}"`),h+=t+l.columnDelimiter}h=h.trim().substring(0,h.length-1),h+=l.lineDelimiter}h=h.trim().substring(0,h.length-1),l.download&&(h=`data:text/csv;charset=utf-8,${h}`)}else if("sql"===l.type){for(h=`INSERT INTO \`${l.tableName}\` (`,n=0;n<e.length;n++)!l.skipColumn.includes(e[n].originalCellIndex)&&this.columns(e[n].originalCellIndex).visible()&&(h+=`\`${e[n].textContent}\`,`);for(h=h.trim().substring(0,h.length-1),h+=") VALUES ",n=0;n<s.length;n++){for(h+="(",r=0;r<s[n].cells.length;r++)!l.skipColumn.includes(e[r].originalCellIndex)&&this.columns(e[r].originalCellIndex).visible()&&(h+=`"${s[n].cells[r].textContent}",`);h=h.trim().substring(0,h.length-1),h+="),"}h=h.trim().substring(0,h.length-1),h+=";",l.download&&(h=`data:application/sql;charset=utf-8,${h}`)}else if("json"===l.type){for(r=0;r<s.length;r++)for(a[r]=a[r]||{},n=0;n<e.length;n++)!l.skipColumn.includes(e[n].originalCellIndex)&&this.columns(e[n].originalCellIndex).visible()&&(a[r][e[n].textContent]=s[r].cells[n].textContent);h=JSON.stringify(a,l.replacer,l.space),l.download&&(h=`data:application/json;charset=utf-8,${h}`)}return l.download&&(l.filename=l.filename||"datatable_export",l.filename+=`.${l.type}`,h=encodeURI(h),(o=document.createElement("a")).href=h,o.download=l.filename,document.body.appendChild(o),o.click(),document.body.removeChild(o)),h}}return!1}import(t){let e=!1;if(!i(t))return!1;const s={lineDelimiter:"\n",columnDelimiter:",",...t};if(s.data.length||i(s.data)){if("csv"===s.type){e={data:[]};const t=s.data.split(s.lineDelimiter);t.length&&(s.headings&&(e.headings=t[0].split(s.columnDelimiter),t.shift()),t.forEach((t,i)=>{e.data[i]=[];const a=t.split(s.columnDelimiter);a.length&&a.forEach(t=>{e.data[i].push(t)})}))}else if("json"===s.type){const t=a(s.data);t&&(e={headings:[],data:[]},t.forEach((t,s)=>{e.data[s]=[],Object.entries(t).forEach(([t,i])=>{e.headings.includes(t)||e.headings.push(t),e.data[s].push(i)})}))}i(s.data)&&(e=s.data),e&&this.insert(e)}return!1}print(){const t=this.activeHeadings,e=this.activeRows,s=n("table"),i=n("thead"),a=n("tbody"),r=n("tr");t.forEach(t=>{r.appendChild(n("th",{html:t.textContent}))}),i.appendChild(r),e.forEach(t=>{const e=n("tr");Array.from(t.cells).forEach(t=>{e.appendChild(n("td",{html:t.textContent}))}),a.appendChild(e)}),s.appendChild(i),s.appendChild(a);const h=window.open();h.document.body.appendChild(s),h.print()}setMessage(t){let e=1;this.hasRows?e=this.data[0].cells.length:this.activeHeadings.length&&(e=this.activeHeadings.length),this.wrapper.classList.add("dataTable-empty"),this.label&&(this.label.innerHTML=""),this.totalPages=0,this.render("pager"),this.clear(n("tr",{html:`<td class="dataTables-empty" colspan="${e}">${t}</td>`}))}columns(t){return new c(this,t)}rows(t){return new d(this,t)}on(t,e){this.events=this.events||{},this.events[t]=this.events[t]||[],this.events[t].push(e)}off(t,e){this.events=this.events||{},t in this.events!=0&&this.events[t].splice(this.events[t].indexOf(e),1)}emit(t){if(this.events=this.events||{},t in this.events!=0)for(let e=0;e<this.events[t].length;e++)this.events[t][e].apply(this,Array.prototype.slice.call(arguments,1))}}s.DataTable=f},{"./date-4abbfef1.js":1}]},{},[2])(2)});
//# sourceMappingURL=/sm/706cee09520d467dac03f066b1e239d985edf633113dca444c8038ac6c04f0de.map
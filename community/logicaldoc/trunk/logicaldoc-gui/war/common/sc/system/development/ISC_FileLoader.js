
/*

  SmartClient Ajax RIA system
  Version 8.0/LGPL Development Only (2010-03-03)

  Copyright 2000 and beyond Isomorphic Software, Inc. All rights reserved.
  "SmartClient" is a trademark of Isomorphic Software, Inc.

  LICENSE NOTICE
     INSTALLATION OR USE OF THIS SOFTWARE INDICATES YOUR ACCEPTANCE OF
     ISOMORPHIC SOFTWARE LICENSE TERMS. If you have received this file
     without an accompanying Isomorphic Software license file, please
     contact licensing@isomorphic.com for details. Unauthorized copying and
     use of this software is a violation of international copyright law.

  DEVELOPMENT ONLY - DO NOT DEPLOY
     This software is provided for evaluation, training, and development
     purposes only. It may include supplementary components that are not
     licensed for deployment. The separate DEPLOY package for this release
     contains SmartClient components that are licensed for deployment.

  PROPRIETARY & PROTECTED MATERIAL
     This software contains proprietary materials that are protected by
     contract and intellectual property law. You are expressly prohibited
     from attempting to reverse engineer this software or modify this
     software for human readability.

  CONTACT ISOMORPHIC
     For more information regarding license rights and restrictions, or to
     report possible license violations, please contact Isomorphic Software
     by email (licensing@isomorphic.com) or web (www.isomorphic.com).

*/

var isc = window.isc ? window.isc : {};if(window.isc&&!window.isc.module_FileLoader){isc.module_FileLoader=1;isc._moduleStart=isc._FileLoader_start=(isc.timestamp?isc.timestamp():new Date().getTime());if(isc._moduleEnd&&(!isc.Log||(isc.Log && isc.Log.logIsDebugEnabled('loadTime')))){isc._pTM={ message:'FileLoader load/parse time: ' + (isc._moduleStart-isc._moduleEnd) + 'ms', category:'loadTime'};
if(isc.Log && isc.Log.logDebug)isc.Log.logDebug(isc._pTM.message,'loadTime')
else if(isc._preLog)isc._preLog[isc._preLog.length]=isc._pTM
else isc._preLog=[isc._pTM]}var isc=window.isc?window.isc:{};isc.$d=new Date().getTime();isc.version="8.0/LGPL Development Only";isc.versionNumber="8.0";isc.buildDate="2010-03-03";isc.expirationDate="";isc.licenseType="LGPL";isc.licenseCompany="Isomorphic Software";isc.licenseSerialNumber="ISC_LGPL_7beta4";isc.licensingPage="http://smartclient.com/product/";isc.$41r={SCServer:{present:"false",name:"SmartClient Server",serverOnly:true,isPro:true},Drawing:{present:"false",name:"Drawing Module"},PluginBridges:{present:"true",name:"PluginBridges Module"},RichTextEditor:{present:"true",name:"RichTextEditor Module"},Calendar:{present:"true",name:"Calendar Module"},Analytics:{present:"false",name:"Analytics Module"},NetworkPerformance:{present:"false",name:"Network Performance Module"},FileLoader:{present:"false",name:"Network Performance Module"},RealtimeMessaging:{present:"false",name:"RealtimeMessaging Module"},serverCriteria:{present:"false",name:"Server Advanced Filtering",serverOnly:true,isFeature:true},customSQL:{present:"false",name:"SQL Templating",serverOnly:true,isFeature:true},chaining:{present:"false",name:"Transaction Chaining",serverOnly:true,isFeature:true},batchDSGenerator:{present:"false",name:"Batch DS-Generator",serverOnly:true,isFeature:true},batchUploader:{present:"false",name:"Batch Uploader",serverOnly:true,isFeature:true}};isc.canonicalizeModules=function(_1){if(!_1)return null;if(isc.isA.String(_1)){if(_1.indexOf(",")!=-1){_1=_1.split(",");var _2=/^\s+/,_3=/\s+$/;for(var i=0;i<_1.length;i++){_1[i]=_1[i].replace(_2,"").replace(_3,"")}}else _1=[_1]}
return _1};isc.hasOptionalModules=function(_1){if(!_1)return true;_1=isc.canonicalizeModules(_1);for(var i=0;i<_1.length;i++)if(!isc.hasOptionalModule(_1[i]))return false;return true};isc.getMissingModules=function(_1){var _2=[];_1=isc.canonicalizeModules(_1);for(var i=0;i<_1.length;i++){var _4=_1[i];if(!isc.hasOptionalModule(_4))_2.add(isc.$41r[_4])}
return _2};isc.hasOptionalModule=function(_1){var v=isc.$41r[_1];if(!v){if(isc.Log)isc.Log.logWarn("isc.hasOptionalModule - unknown module: "+_1);return false}
return v.present=="true"||v.present.charAt(0)=="$"};isc.getOptionalModule=function(_1){return isc.$41r[_1]};isc.$a=window.isc_useSimpleNames;if(isc.$a==null)isc.$a=true;if(window.OpenAjax){isc.$b=isc.versionNumber.replace(/[a-zA-Z_]+/,".0");OpenAjax.registerLibrary("SmartClient","http://smartclient.com/SmartClient",isc.$b,{namespacedMode:!isc.$a,iscVersion:isc.version,buildDate:isc.buildDate,licenseType:isc.licenseType,licenseCompany:isc.licenseCompany,licenseSerialNumber:isc.licenseSerialNumber});OpenAjax.registerGlobals("SmartClient",["isc"])}
isc.$e=window.isc_useLongDOMIDs;isc.$f="isc.";isc.addGlobal=function(_1,_2){if(_1.indexOf(isc.$f)==0)_1=_1.substring(4);isc[_1]=_2;if(isc.$a)window[_1]=_2}
isc.onLine=true;isc.isOffline=function(){return!isc.onLine};isc.goOffline=function(){isc.onLine=false};isc.goOnline=function(){isc.onLine=true};if(window.addEventListener){window.addEventListener("online",isc.goOnline,false);window.addEventListener("offline",isc.goOffline,false)}
isc.addGlobal("Browser",{isSupported:false});isc.Browser.isOpera=(navigator.appName=="Opera"||navigator.userAgent.indexOf("Opera")!=-1);isc.Browser.isNS=(navigator.appName=="Netscape"&&!isc.Browser.isOpera);isc.Browser.isIE=(navigator.appName=="Microsoft Internet Explorer"&&!isc.Browser.isOpera);isc.Browser.isMSN=(isc.Browser.isIE&&navigator.userAgent.indexOf("MSN")!=-1);isc.Browser.minorVersion=parseFloat(isc.Browser.isIE?navigator.appVersion.substring(navigator.appVersion.indexOf("MSIE")+5):navigator.appVersion);isc.Browser.version=parseInt(isc.Browser.minorVersion);isc.Browser.isIE6=isc.Browser.isIE&&isc.Browser.version<=6;isc.Browser.isMoz=(navigator.userAgent.indexOf("Gecko")!=-1)&&(navigator.userAgent.indexOf("Safari")==-1)&&(navigator.userAgent.indexOf("AppleWebKit")==-1);isc.Browser.isCamino=(isc.Browser.isMoz&&navigator.userAgent.indexOf("Camino/")!=-1);if(isc.Browser.isCamino){isc.Browser.caminoVersion=navigator.userAgent.substring(navigator.userAgent.indexOf("Camino/")+7)}
isc.Browser.isFirefox=(isc.Browser.isMoz&&navigator.userAgent.indexOf("Firefox/")!=-1);if(isc.Browser.isFirefox){isc.Browser.firefoxVersion=navigator.userAgent.substring(navigator.userAgent.indexOf("Firefox/")+8)}
if(isc.Browser.isMoz){isc.Browser.$g=navigator.userAgent.indexOf("Gecko/")+6;isc.Browser.geckoVersion=parseInt(navigator.userAgent.substring(isc.Browser.$g,isc.Browser.$g+8));if(isc.Browser.isFirefox){if(isc.Browser.firefoxVersion.match(/^1\.0/))isc.Browser.geckoVersion=20050915;else if(isc.Browser.firefoxVersion.match(/^2\.0/))isc.Browser.geckoVersion=20071108}}
isc.Browser.isStrict=document.compatMode=="CSS1Compat";if(isc.Browser.isStrict&&isc.Browser.isMoz){isc.Browser.$51p=document.doctype.publicId;isc.Browser.$51q=document.doctype.systemId;isc.Browser.isTransitional=isc.Browser.$51p.indexOf("Transitional")!=-1||isc.Browser.$51p.indexOf("Frameset")!=-1}
isc.Browser.isIE8=isc.Browser.isIE&&isc.Browser.version>=8&&document.documentMode>7
isc.Browser.isIE8Strict=isc.Browser.isIE&&isc.Browser.isStrict&&isc.Browser.isIE8;isc.Browser.isBorderBox=isc.Browser.isIE&&!isc.Browser.isStrict;isc.Browser.isAIR=(navigator.userAgent.indexOf("AdobeAIR")!=-1);isc.Browser.AIRVersion=(isc.Browser.isAIR?navigator.userAgent.substring(navigator.userAgent.indexOf("AdobeAir/")+9):null);isc.Browser.isSafari=isc.Browser.isAIR||navigator.userAgent.indexOf("Safari")!=-1||navigator.userAgent.indexOf("AppleWebKit")!=-1;isc.Browser.isChrome=isc.Browser.isSafari&&(navigator.userAgent.indexOf("Chrome/")!=-1);if(isc.Browser.isSafari){if(isc.Browser.isAIR){isc.Browser.safariVersion=530}else{if(navigator.userAgent.indexOf("Safari/")!=-1){isc.Browser.rawSafariVersion=navigator.userAgent.substring(navigator.userAgent.indexOf("Safari/")+7)}else if(navigator.userAgent.indexOf("AppleWebKit/")!=-1){isc.Browser.rawSafariVersion=navigator.userAgent.substring(navigator.userAgent.indexOf("AppleWebKit/")+12)}else{isc.Browser.rawSafariVersion="530"}
isc.Browser.safariVersion=(function(){var _1=isc.Browser.rawSafariVersion,_2=_1.indexOf(".");if(_2==-1)return parseInt(_1);var _3=_1.substring(0,_2+1),_4;while(_2!=-1){_2+=1;_4=_1.indexOf(".",_2);_3+=_1.substring(_2,(_4==-1?_1.length:_4));_2=_4}
return parseFloat(_3)})()}}
isc.Browser.isWin=navigator.platform.toLowerCase().indexOf("win")>-1;isc.Browser.isWin2k=navigator.userAgent.match(/NT 5.01?/)!=null;isc.Browser.isMac=navigator.platform.toLowerCase().indexOf("mac")>-1;isc.Browser.isUnix=(!isc.Browser.isMac&&!isc.Browser.isWin);isc.Browser.lineFeed=(isc.Browser.isWin?"\r\n":"\r");isc.Browser.$h=false;isc.Browser.isDOM=(isc.Browser.isMoz||isc.Browser.isOpera||isc.Browser.isSafari||(isc.Browser.isIE&&isc.Browser.version>=5));isc.Browser.isSupported=((isc.Browser.isIE&&isc.Browser.minorVersion>=5.5&&isc.Browser.isWin)||isc.Browser.isMoz||isc.Browser.isOpera||isc.Browser.isSafari||isc.Browser.isAIR);isc.Browser.allowsXSXHR=((isc.Browser.isFirefox&&isc.Browser.firefoxVersion>="3.5")||(isc.Browser.isChrome)||(isc.Browser.isSafari&&isc.Browser.safariVersion>=531));if(isc.addProperties==null){isc.addGlobal("addProperties",function(_1,_2){for(var _3 in _2)
_1[_3]=_2[_3];return _1})}
isc.addGlobal("evalSA",function(_1){if(isc.eval)isc.eval(_1);else eval(_1)});isc.addGlobal("defineStandaloneClass",function(_1,_2){if(isc[_1])return;isc.addGlobal(_1,_2);isc.addProperties(_2,{$i:_1,fireSimpleCallback:function(_3){_3.method.apply(_3.target?_3.target:window,_3.args?_3.args:[])},logMessage:function(_3,_4,_5){if(isc.Log){isc.Log.logMessage(_3,_4,_5);return}
if(!isc.$j)isc.$j=[];isc.$j[isc.$j.length]={priority:_3,message:_4,category:_5,timestamp:new Date()}},logWarn:function(_3){this.logMessage(3,_3,this.$i)},logInfo:function(_3){this.logMessage(4,_3,this.$i)},logDebug:function(_3){this.logMessage(5,_3,this.$i)},isA:{String:function(_3){if(_3==null)return false;if(_3.constructor&&_3.constructor.$k!=null){return _3.constructor.$k==4}
return typeof _3=="string"}}});_2.isAn=_2.isA;return _2});isc.defineStandaloneClass("SA_Page",{$l:false,$m:[],isLoaded:function(){return this.$l},onLoad:function(_1,_2,_3){this.$m.push({method:_1,target:_2,args:_3});if(!this.$n){this.$n=true;if(isc.Browser.isIE||isc.Browser.isOpera){window.attachEvent("onload",function(){isc.SA_Page.$o()})}else{window.addEventListener("load",function(){isc.SA_Page.$o()},true)}}},$o:function(){if(!window.isc||this.$l)return;this.$l=true;for(var i=0;i<this.$m.length;i++){var _2=this.$m[i];this.fireSimpleCallback(_2)}
delete this.$m}});isc.SA_Page.onLoad(function(){this.$l=true},isc.SA_Page);isc.defineStandaloneClass("SA_XMLHttp",{$401:function(){var _1=arguments.callee.xmlHttpRequest;if(!_1)return;if(_1.readyState!=4)return;arguments.callee.xmlHttpRequest=null;var _2=arguments.callee.callback;if(_2)isc.SA_XMLHttp.$402(_2,_1)},$402:function(_1,_2){var _3=[_2];if(_1.args)_1.args=_1.args.concat(_3);else _1.args=_3;this.fireSimpleCallback(_1)},get:function(_1,_2){var _3=this.createXMLHttpRequest();if(!_3){this.logWarn("XMLHttpRequest not available - can't fetch url: "+_1);return}
_3.open("GET",_1,true);if(isc.Browser.isIE){var _4=this.$401;_4.callback=_2;_4.xmlHttpRequest=_3;_3.onreadystatechange=_4}else{_3.onreadystatechange=function(){if(_3.readyState!=4)return;isc.SA_XMLHttp.$402(_2,_3)}}
_3.send(null);return _3},xmlHttpConstructors:["MSXML2.XMLHTTP","Microsoft.XMLHTTP","MSXML.XMLHTTP","MSXML3.XMLHTTP"],createXMLHttpRequest:function(){if(isc.Browser.isIE){var _1;if(isc.preferNativeXMLHttpRequest){_1=this.getNativeRequest();if(!_1)_1=this.getActiveXRequest()}else{_1=this.getActiveXRequest();if(!_1)_1=this.getNativeRequest()}
if(!_1)this.logWarn("Couldn't create XMLHttpRequest");return _1}else{return new XMLHttpRequest()}},getNativeRequest:function(){var _1;if(isc.Browser.version>=7){this.logDebug("Using native XMLHttpRequest");_1=new XMLHttpRequest()}
return _1},getActiveXRequest:function(){var _1;if(!this.$h4){for(var i=0;i<this.xmlHttpConstructors.length;i++){try{var _3=this.xmlHttpConstructors[i];_1=new ActiveXObject(_3);if(_1){this.$h4=_3;break}}catch(e){}}}else{_1=new ActiveXObject(this.$h4)}
if(_1)this.logDebug("Using ActiveX XMLHttpRequest via constructor: "+this.$h4);return _1}});if(!window.isc_maxCSSLoaders)window.isc_maxCSSLoaders=20;isc.defineStandaloneClass("FileLoader",{$403:new Date().getTime(),disableCaching:false,versionParamName:"isc_version",addVersionToLoadTags:true,modulesDir:"system/modules/",cssPollFrequency:50,cssLoadTimeout:2000,cssWarnTimeout:1000,nextCSSLoader:0,$d4:null,$404:[],$405:[],$406:{},defaultModules:"Core,Foundation,Containers,Grids,Forms,DataBinding",defaultSkin:"standard",getIsomorphicDir:function(){return window.isomorphicDir?window.isomorphicDir:"../isomorphic/"},cacheISC:function(_1,_2,_3){this.cacheModules(_2?_2:this.defaultModules);this.cacheSkin(_1,_3)},cacheSkin:function(_1,_2){var _3=this.$407(_1);this.cacheFile(_3+"load_skin.js");this.cacheFile(_3+"skin_styles.css",_2)},loadISC:function(_1,_2,_3){this.loadModules(_2?_2:this.defaultModules);this.loadSkin(_1,_3)},loadSkin:function(_1,_2){var _3=this.$407(_1);if(!this.$hw)this.$hw=[];this.$hw[this.$hw.length]=_3+"skin_styles.css";this.loadFile(_3+"skin_styles.css");this.loadFile(_3+"load_skin.js",_2)},$407:function(_1){if(!_1)_1=this.defaultSkin;var _2;if(_1.indexOf("/")!=-1){_2=_1}else{_2=this.getIsomorphicDir()+"skins/"+_1+"/"}
if(_2.charAt(_2.length-1)!="/")_2+="/";return _2},loadJSFile:function(_1,_2){this.$408(_1,_2,"js",{defer:true})},loadModule:function(_1,_2){this.$408(_1,_2,"js",{defer:true,isModule:true})},cacheFile:function(_1,_2,_3){this.$408(_1,_2,_3,{cacheOnly:true})},cacheModule:function(_1,_2){if(isc.Browser.isMoz&&isc.Browser.geckoVersion<20051107){_1=this.$409(_1);for(var i=0;i<_1.length;i++){isc["module_"+_1[i]]=1}
this.loadModules(_1,_2);return}
this.$408(_1,_2,"js",{cacheOnly:true,isModule:true})},loadCSSFile:function(_1,_2){this.$408(_1,_2,"css",{defer:true})},loadFile:function(_1,_2,_3){this.$408(_1,_2,_3,{defer:true})},$41a:/(.*)\.(.*)/,defaultImageStates:"Down,Over,Disabled",cacheImgStates:function(_1,_2,_3){var _4=this.addURLSuffix(_1,_2!=null?_2:this.defaultImageStates);this.cacheFiles(_4,_3,"image")},cacheStretchImgStates:function(_1,_2,_3,_4){if(_3==null)_3="start,stretch,end";var _5=this.addURLSuffix(_1,_3);var _6=this.addURLSuffix(_1,_2!=null?_2:this.defaultImageStates);_5=_5.concat(this.addURLSuffix(_6,_3));this.cacheFiles(_5,_4,"image")},defaultEdges:"TL,T,TR,L,R,BL,B,BR",defaultEdgeColors:"",cacheEdgeImages:function(_1,_2,_3,_4,_5){_1=this.$409(_1);if(_3==null)_3=this.defaultEdges;_3=this.$409(_3);if(_2)_3[_3.length]="center";if(_4==null)_4=this.defaultEdgeColors;var _6=_1;if(_4.length)_6=this.addURLSuffix(_6,_4);_6=this.addURLSuffix(_6,_3);this.cacheFiles(_6,_5,"image")},defaultBaseShadowImage:"ds.png",cacheShadows:function(_1,_2,_3,_4){_2=this.$409(_2);if(_3==null)_3=this.defaultBaseShadowImage;var _5=this.$41a.exec(_3);if(!_5){this.logWarn("Couldn't split baseShadowImage '"+_3+"' into basePath and extension - file will not be cached.");return}
var _6=_5[1];var _7=_5[2];if(_1.charAt(_1.length-1)!="/")_1=_1+"/";var _8="_";this.cacheFile(_1+_6+_8+"center."+_7,_4,"image");for(var i=0;i<_2.length;i++)
this.cacheEdgeImages(_1+_6+_2[i]+"."+_7,false,null,null,_4)},addURLSuffix:function(_1,_2){_1=this.$409(_1);_2=this.$409(_2);var _3=[];for(var i=0;i<_1.length;i++){var _5=_1[i];var _6=_5.indexOf("?");var _7="";if(_6!=-1){_5=_5.substring(0,_6);_7=_5.substring(_6,_5.length)}
var _8=this.$41a.exec(_5);if(!_8){this.logWarn("Couldn't split baseURL '"+_5+"' into basePath and extension - file will not be cached.");continue}
var _9=_8[1];var _10=_8[2];for(var j=0;j<_2.length;j++){_3[_3.length]=_9+"_"+_2[j]+"."+_10+_7}}
return _3},$409:function(_1){var _2;if(!_1)return[];if(this.isA.String(_1))_1=_1.split(",");var _3=[];for(var i=0;i<_1.length;i++){var _5=_1[i];_3[i]=_5.replace(/\s+/g,"")}
return _3},$408:function(_1,_2,_3,_4){_1=this.$409(_1);var _5=false,_6;for(var i=0;i<_1.length;i++){var _8=_1[i];if(_4.isModule){if(!_4.cacheOnly){var _9=_8;if(isc.$41r[_9]&&isc.$41r[_9].isFeature)continue;if(_9.indexOf("ISC_")==0)_9=_9.substring(4);if(isc["module_"+_9]){this.logWarn("Suppressed duplicate load of module: "+_9);continue}
if(isc.$41r[_9]&&isc.$41r[_9].serverOnly)continue}
if(_8.indexOf("ISC_")!=0&&_8.indexOf("/")==-1)_8="ISC_"+_8;if(_8.indexOf("/")==-1)_8=this.getIsomorphicDir()+this.modulesDir+_8+".js"}
if(this.disableCaching&&!_4.cacheOnly){_8+=(_8.indexOf("?")!=-1?"&":"?")+"ts="+(new Date().getTime())}
if(_4.isModule&&this.addVersionToLoadTags){var _10=isc.versionNumber;if(_10.indexOf("${")==0)_10="dev";_8+=(_8.indexOf("?")!=-1?"&":"?")+this.versionParamName+"="+_10+".js"}
var _11=_8+"_"+this.$403+"_"+new Date().getTime();var _12=this.$406[_11]={fileID:_11,URL:_8,type:_3}
if(_4)for(var _13 in _4)_12[_13]=_4[_13];if(_12.type==null){var _14=_8;var _15=_14.indexOf("?");if(_15!=-1)_14=_14.substring(0,_15);if(_14.match(/\.js$/i))_12.type="js";else if(_14.match(/\.css$/i))_12.type="css";else if(_14.match(/\.(gif|png|tiff|tif|bmp|dib|ief|jpe|jpeg|jpg|pbm|pct|pgm|pic|pict|ico)$/i))
_12.type="image";if(_12.type==null){this.logWarn("Unable to autodetect file type for URL: "+_8+" please specify it explicitly in your call to"+" isc.FileLoader.cacheFile()/isc.FileLoader.loadFile()."+" Ignoring this file.");delete this.$406[_11];return}}
if(_12.type=="image"){_5=true;_6=_12;this.$404.push(_11)}else{if(isc.Browser.isMoz&&isc.Browser.geckoVersion<20051107&&_12.cacheOnly&&!_4.isModule)
{delete this.$406[_11];continue}else{this.logInfo("queueing URL: "+_12.URL+", type: "+_12.type+", onload: "+_12.onload);this.$405.push(_11);_5=true;_6=_12}}}
if(_5&&_2)_6.onload=_2;if(!_5&&_2){if(this.isA.String(_2))isc.evalSA(_2);else _2();return}
this.$41b()},$41c:function(){var _1="";while(this.$404.length){var _2=this.$404.shift();var _3=this.$406[_2].URL;var _4="if(window.isc)isc.FileLoader.fileLoaded(\""+_2+"\")";_1+="<IMG SRC='"+_3+"' onload='"+_4+"' onerror='"+_4+"' onabort='"+_4+(isc.Browser.isOpera?"' STYLE=visibility:hidden;position:absolute;top:-1000px'>":"' STYLE='display:none'>")}
this.$p8(_1)},$41b:function(){if(!isc.SA_Page.isLoaded())return;this.$49g=true;if(this.$405.length){if(this.$41d){return}
var _1=this.$405.shift();var _2=this.$406[_1];var _3=_2.URL;this.$41d=true;if(_2.defer){this.$41e(_1)}else{this.$41f(_1)}}else{this.$41c()}
this.$49g=false},$41e:function(_1){var _2=this.$406[_1];var _3=_2.URL;var _4=_2.type;if(_4=="js"){if(isc.Browser.isOpera){this.$49h(_3,"isc.FileLoader.fileLoaded('"+_1+"')")}else if(isc.Browser.isMoz&&isc.Browser.geckoVersion<20051107){this.$p8("<SCRIPT SRC='"+_3+"'></SCRIPT><SCRIPT>if(window.isc)isc.FileLoader.fileLoaded('"+_1+"')</SCRIPT>")}else{isc.SA_XMLHttp.get(_3,{method:this.fileLoaded,target:this,args:[_1]})}}else if(_4=="css"){_2.cssIndex=isc.Browser.isSafari?this.nextCSSLoader:document.styleSheets.length;_2.cssLoadStart=new Date().getTime();if(isc.Browser.isSafari){if(this.nextCSSLoader>window.isc_maxCSSLoaders){this.logWarn("maxCSSLoaders ("+window.isc_maxCSSLoaders+") exceeded - can't load "+_2.URL+" set isc_maxCSSLoaders to a larger number.");this.fileLoaded(_1);return}
this.$41g().href=_3}else{this.$41h(_3)}
this.startCSSPollTimer(_1,0)}},startCSSPollTimer:function(_1,_2){window.setTimeout("isc.FileLoader.pollForCSSLoaded('"+_1+"')",_2)},pollForCSSLoaded:function(_1){var _2=this.$406[_1];var _3=document.styleSheets[_2.cssIndex];var _4=false;if(_3==null){this.logWarn("Can't find cssRule for URL: "+_2.URL+" at index: "+_2.cssIndex)}else{if(isc.Browser.isIE){if(_3.rules!=null&&_3.rules.length>0)_4=true}else if(isc.Browser.isOpera){if(_3.cssRules!=null&&_3.cssRules.length>0)_4=true}else{try{if(_3.cssRules!=null&&_3.cssRules.length>0)_4=true}catch(e){if(isc.Browser.isMoz&&(document.domain!=location.hostname||(_2.URL.startsWith("http")&&_2.URL.indexOf(location.hostname)==-1)))
{_4=true}}}}
if(!_4){var _5=new Date().getTime();if(_5>_2.cssLoadStart+this.cssWarnTimeout&&!_2.warnedAboutCSSTimeout){this.logWarn("CSS file "+_2.URL+" taking longer than "+this.cssWarnTimeout+" to load - may indicate a bad URL");_2.warnedAboutCSSTimeout=true}
if(_5>_2.cssLoadStart+this.cssLoadTimeout){this.logWarn("cssLoadTimeout of: "+this.cssLoadTimeout+" exceeded for: "+_2.URL+" - assuming loaded, firing onload handler.");_4=true}}
if(_4){this.fileLoaded(_1)}else{this.startCSSPollTimer(_1,this.cssPollFrequency)}},$41f:function(_1){var _2=this.$406[_1];var _3=_2.URL;if(isc.Browser.isOpera){this.$49h(_3,"isc.FileLoader.fileLoaded('"+_1+"')","text/html")}else if(isc.Browser.isIE||isc.Browser.isSafari||(isc.Browser.isMoz&&isc.Browser.geckoVersion>=20051107))
{isc.SA_XMLHttp.get(_3,{method:this.fileLoaded,target:this,args:[_1]})}else if(isc.Browser.isMoz){var _4=this.$41i();this.$41j=_1;_4.src=_3}},fileLoaded:function(_1,_2,_3,_4){if(!window.isc)return;if(_2!=null&&_2.responseText)
_2=_2.responseText;if(!_1){_1=this.$41j;delete this.$41j}
var _5=this.$406[_1];if(!_5){return}
if(_5.defer&&_5.type=="js"&&_2){_5.fileContents=_2;window.setTimeout("isc.FileLoader.delayedEval('"+_1+"')",0)}else{this.$41k(_1)}
if(_5.type!="image"){this.$41d=false}
if(this.$49g){window.setTimeout("isc.FileLoader.$41b()",0)}else this.$41b()},delayedEval:function(_1){var _2=this.$406[_1];var _3=_2.fileContents;if(isc.Browser.isSafari){window.setTimeout([_3,";isc.FileLoader.$41k('",_1,"')"].join(""),0);return}else if(isc.Browser.isIE){window.execScript(_3,"javascript")}else{if(isc.Class&&isc.Class.evaluate){isc.Class.evaluate(_3,null,true)}else{window.eval(_3)}}
this.$41k(_1)},$41k:function(_1){var _2=this.$406[_1];this.$41l();if(_2.onload){if(this.isA.String(_2.onload))isc.evalSA(_2.onload);else _2.onload(_2)}
delete this.$406[_1]},$41i:function(){if(!this.$222){this.$p8("<IFRAME STYLE='position:absolute;visibility:hidden;top:-1000px'"+" onload='if(window.isc)isc.FileLoader.fileLoaded()'"+" NAME='isc_fileLoader_iframe' ID='isc_fileLoader_iframe'></IFRAME>");this.$222=document.getElementById("isc_fileLoader_iframe")}
return this.$222},$p8:function(_1){if(!this.$41m)this.$41m=document.getElementsByTagName("body")[0];var _2=this.$41m;if(isc.Browser.isIE||isc.Browser.isOpera){_2.insertAdjacentHTML('beforeEnd',_1)}else{var _3=_2.ownerDocument.createRange();_3.setStartBefore(_2);var _4=_3.createContextualFragment(_1);_2.appendChild(_4)}},$41h:function(_1,_2){var e=document.createElement("link");e.rel="stylesheet";e.type="text/css";e.href=_1;if(_2)e.onload=_2;document.getElementsByTagName("body")[0].appendChild(e)},$49h:function(_1,_2,_3){if(!_3)_3="text/javascript";var e=document.createElement("script");e.type=_3
e.src=_1;if(_2)e.onload=_2;document.getElementsByTagName("body")[0].appendChild(e)},$41n:function(){for(var i=0;i<this.$405.length;i++){var _2=this.$405[i];var _3=this.$406[_2];if(_3.isModule)return true}
return false},$41l:function(){if(isc.Page&&!isc.Page.isLoaded()){isc.Page.finishedLoading()}},$41o:function(){this.logInfo("FileLoader initialized");setTimeout("isc.FileLoader.$41b()",0)},$41g:function(_1){if(_1==null)_1=this.nextCSSLoader++;return document.getElementById("isc_fl_css_loader"+_1)},throbberStyle:"throbber",throbberTextStyle:"throbberText",throbberImgSrc:"[SKIN]loading.gif",throbberImgWidth:48,throbberImgHeight:48,throbberWidth:270,throbberHeight:60,showThrobber:function(_1,_2,_3,_4,_5){this.hideThrobber();_2=_2||this.throbberTextStyle;_3=_3||this.throbberImgSrc;_4=_4||this.throbberImgWidth;_5=_5||this.throbberImgHeight;var _6=this.$58j=document.createElement("DIV");_6.className=this.throbberStyle;_6.style.position="absolute";_6.style.left="50%";_6.style.top="50%";_6.style.width=this.throbberWidth+"px";_6.style.height=this.throbberHeight+"px";_6.style.marginLeft=-(this.throbberWidth/ 2)+"px";_6.style.marginTop=-(this.throbberHeight/ 2)+"px";_6.style.zIndex=1000000000;var _7=document.createElement("TABLE"),_8=document.createElement("TBODY"),_9=document.createElement("TR");_7.height="100%";_7.width="100%";var _10=document.createElement("TD"),_11=document.createElement("IMG");if(_3&&_3.indexOf("[SKIN]")==0){_3=this.$407()+"images/"+_3.substring(6)}
_11.src=_3;_11.height=_5;_10.appendChild(_11);_9.appendChild(_10);if(_1){var _12=document.createTextNode(_1);_10=document.createElement("TD");_10.className=_2;_10.appendChild(_12);_9.appendChild(_10)}
_8.appendChild(_9);_7.appendChild(_8);_6.appendChild(_7);document.getElementsByTagName("body").item(0).appendChild(_6)},hideThrobber:function(){if(this.$58j){document.getElementsByTagName("body").item(0).removeChild(this.$58j);this.$58j=null}}});isc.addGlobal("FL",isc.FileLoader);isc.A=isc.FileLoader;isc.A.loadJSFiles=isc.FileLoader.loadJSFile;isc.A.loadModules=isc.FileLoader.loadModule;isc.A.cacheFiles=isc.FileLoader.cacheFile;isc.A.cacheModules=isc.FileLoader.cacheModule;isc.A.loadCSSFiles=isc.FileLoader.loadCSSFile;isc.A.loadFiles=isc.FileLoader.loadFile;if(isc.Browser.isSafari){var s="";for(var i=0;i<window.isc_maxCSSLoaders;i++){s+="<LINK id='isc_fl_css_loader"+i+"' name='isc_fl_css_loader"+i+"' REL='stylesheet' TYPE='text/css'>"}
document.write(s)}
isc.SA_Page.onLoad(isc.FileLoader.$41o,isc.FileLoader);isc._moduleEnd=isc._FileLoader_end=(isc.timestamp?isc.timestamp():new Date().getTime());if(isc.Log&&isc.Log.logIsInfoEnabled('loadTime'))isc.Log.logInfo('FileLoader module init time: ' + (isc._moduleEnd-isc._moduleStart) + 'ms','loadTime');}else{if(window.isc && isc.Log && isc.Log.logWarn)isc.Log.logWarn("Duplicate load of module 'FileLoader'.");}

/*

  SmartClient Ajax RIA system
  Version 8.0/LGPL Development Only (2010-03-03)

  Copyright 2000 and beyond Isomorphic Software, Inc. All rights reserved.
  "SmartClient" is a trademark of Isomorphic Software, Inc.

  LICENSE NOTICE
     INSTALLATION OR USE OF THIS SOFTWARE INDICATES YOUR ACCEPTANCE OF
     ISOMORPHIC SOFTWARE LICENSE TERMS. If you have received this file
     without an accompanying Isomorphic Software license file, please
     contact licensing@isomorphic.com for details. Unauthorized copying and
     use of this software is a violation of international copyright law.

  DEVELOPMENT ONLY - DO NOT DEPLOY
     This software is provided for evaluation, training, and development
     purposes only. It may include supplementary components that are not
     licensed for deployment. The separate DEPLOY package for this release
     contains SmartClient components that are licensed for deployment.

  PROPRIETARY & PROTECTED MATERIAL
     This software contains proprietary materials that are protected by
     contract and intellectual property law. You are expressly prohibited
     from attempting to reverse engineer this software or modify this
     software for human readability.

  CONTACT ISOMORPHIC
     For more information regarding license rights and restrictions, or to
     report possible license violations, please contact Isomorphic Software
     by email (licensing@isomorphic.com) or web (www.isomorphic.com).

*/


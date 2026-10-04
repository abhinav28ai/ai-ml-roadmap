const CACHE="aiml-roadmap-v4";
const PRECACHE=["./","./index.html","./manifest.webmanifest","./roadmap-data.json","./icons/icon-192.png","./icons/icon-512.png"];
self.addEventListener("install",event=>event.waitUntil(caches.open(CACHE).then(c=>c.addAll(PRECACHE)).then(()=>self.skipWaiting())));
self.addEventListener("activate",event=>event.waitUntil(caches.keys().then(keys=>Promise.all(keys.filter(k=>k!==CACHE).map(k=>caches.delete(k)))).then(()=>self.clients.claim())));
self.addEventListener("fetch",event=>{
 if(event.request.method!=="GET")return;
 const url=new URL(event.request.url); if(url.origin!==location.origin)return;
 const htmlReq=event.request.mode==="navigate"||url.pathname.endsWith("/")||url.pathname.endsWith(".html");
 if(htmlReq){event.respondWith(fetch(event.request,{cache:"no-store"}).then(r=>{const c=r.clone();caches.open(CACHE).then(x=>x.put("./index.html",c));return r}).catch(()=>caches.match("./index.html")))}
 else event.respondWith(caches.match(event.request).then(c=>c||fetch(event.request)));
});
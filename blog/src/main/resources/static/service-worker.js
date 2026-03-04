const CACHE_NAME = 'rod-blog-cache-v1';
const urlsToCache = [
  '/',
  '/css/style.css',
  '/css/theme_transition.css',
  '/js/script.js',
  '/js/theme_toggle.js',
  '/js/notification.js',
  '/img/icons/icon-192x192.png',
  '/img/icons/icon-512x512.png',
  '/favicon.ico',
  '/img/logo.png'
];

self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then(cache => {
        console.log('Opened cache');
        return cache.addAll(urlsToCache);
      })
  );
});

self.addEventListener('fetch', event => {
  // 업로드 이미지나 API 요청은 캐시를 거치지 않고 직접 네트워크 요청
  if (event.request.url.includes('/upload/') || 
      event.request.url.includes('/calendar/') || 
      event.request.url.includes('/todo/')) {
    return; // 브라우저가 직접 처리하도록 위임
  }

  event.respondWith(
    caches.match(event.request)
      .then(response => {
        if (response) {
          return response;
        }
        return fetch(event.request).catch(err => {
          console.log('Fetch failed; returning offline page or error', err);
          // 여기서 오프라인 페이지를 반환하거나 단순히 에러를 던지지 않게 처리 가능
        });
      })
  );
});

self.addEventListener('activate', event => {
  const cacheWhitelist = [CACHE_NAME];
  event.waitUntil(
    caches.keys().then(cacheNames => {
      return Promise.all(
        cacheNames.map(cacheName => {
          if (cacheWhitelist.indexOf(cacheName) === -1) {
            return caches.delete(cacheName);
          }
        })
      );
    })
  );
});
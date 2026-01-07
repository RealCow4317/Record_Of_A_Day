// PWA 설치 및 Service Worker 등록 스크립트

// 개발 모드 체크 (URL에 ?debug=true 또는 localhost)
const isDevMode = window.location.hostname === 'localhost' || 
                  window.location.hostname === '127.0.0.1' ||
                  window.location.search.includes('debug=true');

// Service Worker 등록
if ('serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    navigator.serviceWorker.register('/service-worker.js')
      .then((registration) => {
        console.log('✅ ServiceWorker 등록 성공:', registration.scope);
        if (isDevMode) {
          console.log('🔧 개발 모드: Service Worker 활성화됨');
        }
      })
      .catch((error) => {
        console.error('❌ ServiceWorker 등록 실패:', error);
      });
  });
}

// 설치 프롬프트 표시
let deferredPrompt;
const installButton = document.getElementById('install-button');

window.addEventListener('beforeinstallprompt', (e) => {
  // 기본 설치 프롬프트 방지
  e.preventDefault();
  deferredPrompt = e;
  
  console.log('📱 PWA 설치 프롬프트 이벤트 발생');
  
  // 설치 버튼이 있으면 표시
  if (installButton) {
    installButton.style.display = 'block';
    installButton.addEventListener('click', installApp);
    console.log('✅ 설치 버튼 표시됨');
  }
});

// 개발 모드에서 버튼 강제 표시 (테스트용)
if (isDevMode && installButton) {
  // console.log('🔧 개발 모드: 설치 버튼 강제 표시 (테스트용)');
  // installButton.style.display = 'block';
  installButton.addEventListener('click', () => {
    if (deferredPrompt) {
      installApp();
    } else {
      console.log('⚠️ 설치 프롬프트가 아직 준비되지 않았습니다.');
      console.log('💡 Chrome DevTools > Application > Manifest에서 테스트하세요.');
      // alert('개발 모드: 설치 프롬프트가 아직 준비되지 않았습니다.\n\nChrome DevTools > Application > Manifest에서 PWA를 테스트할 수 있습니다.');
    }
  });
}

// 앱 설치 함수
function installApp() {
  if (!deferredPrompt) {
    return;
  }
  
  // 설치 프롬프트 표시
  deferredPrompt.prompt();
  
  // 사용자 선택 대기
  deferredPrompt.userChoice.then((choiceResult) => {
    if (choiceResult.outcome === 'accepted') {
      console.log('사용자가 앱 설치를 수락했습니다');
    } else {
      console.log('사용자가 앱 설치를 거부했습니다');
    }
    deferredPrompt = null;
    
    // 버튼 숨기기
    if (installButton) {
      installButton.style.display = 'none';
    }
  });
}

// 앱이 이미 설치되어 있는지 확인
window.addEventListener('appinstalled', () => {
  console.log('앱이 설치되었습니다');
  deferredPrompt = null;
  
  if (installButton) {
    installButton.style.display = 'none';
  }
});

// iOS Safari에서 홈 화면에 추가 안내
function isIOS() {
  return /iPad|iPhone|iPod/.test(navigator.userAgent) && !window.MSStream;
}

function isInStandaloneMode() {
  return ('standalone' in window.navigator) && (window.navigator.standalone);
}

// iOS에서 standalone 모드가 아니면 버튼 표시
if (isIOS() && !isInStandaloneMode() && installButton) {
  installButton.style.display = 'block';
  installButton.addEventListener('click', () => {
    alert('Safari 메뉴(공유 버튼)에서 "홈 화면에 추가"를 선택해주세요.');
  });
}

// 이미 설치된 경우 버튼 숨기기
if (isInStandaloneMode() || window.matchMedia('(display-mode: standalone)').matches) {
  if (installButton) {
    installButton.style.display = 'none';
  }
  if (isDevMode) {
    console.log('📱 앱이 이미 설치되어 있습니다 (Standalone 모드)');
  }
}

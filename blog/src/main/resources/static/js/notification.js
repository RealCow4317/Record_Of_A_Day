/**
 * WebSocket을 통한 실시간 알림 수신 및 브라우저 알림 표시
 */
(function() {
    'use strict';

    let stompClient = null;
    let notificationPermission = false;

    /**
     * 브라우저 알림 권한 요청
     */
    function requestNotificationPermission() {
        if ('Notification' in window) {
            if (Notification.permission === 'granted') {
                notificationPermission = true;
            } else if (Notification.permission !== 'denied') {
                Notification.requestPermission().then(function(permission) {
                    notificationPermission = (permission === 'granted');
                });
            }
        }
    }

    /**
     * 브라우저 알림 표시
     */
    function showBrowserNotification(message) {
        if (!notificationPermission || !('Notification' in window)) {
            return;
        }

        // 컨텍스트 패스 가져오기 (있는 경우)
        const contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2)) === "/blog" ? "/blog" : "";
        const iconPath = (window.location.origin + (contextPath || '') + '/resources/favicon.ico');

        const notification = new Notification('RealCowLabs', {
            body: message,
            icon: iconPath,
            badge: iconPath,
            tag: 'evening-notification',
            requireInteraction: false
        });

        // 알림 클릭 시 창 포커스
        notification.onclick = function() {
            window.focus();
            notification.close();
        };

        // 5초 후 자동 닫기
        setTimeout(function() {
            notification.close();
        }, 5000);
    }

    /**
     * WebSocket 연결
     */
    function connect() {
        // SockJS와 STOMP 라이브러리가 로드되어 있는지 확인
        if (typeof SockJS === 'undefined' || typeof Stomp === 'undefined') {
            console.warn('SockJS 또는 STOMP 라이브러리가 로드되지 않았습니다.');
            return;
        }

        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        // 디버그 로그 비활성화 (필요시 주석 해제)
        stompClient.debug = null;

        stompClient.connect({}, function(frame) {
            console.log('WebSocket 연결 성공:', frame);

            // 알림 구독
            stompClient.subscribe('/topic/notifications', function(message) {
                // 메시지가 문자열인 경우 그대로 사용, JSON인 경우 파싱
                let notificationMessage;
                try {
                    notificationMessage = JSON.parse(message.body);
                } catch (e) {
                    notificationMessage = message.body;
                }
                console.log('알림 수신:', notificationMessage);

                // 브라우저 알림 표시
                showBrowserNotification(notificationMessage);

                // 페이지에 알림 표시 (선택사항)
                showPageNotification(notificationMessage);
            });
        }, function(error) {
            console.error('WebSocket 연결 실패:', error);
            // 재연결 시도 (5초 후)
            setTimeout(connect, 5000);
        });
    }

    /**
     * 페이지 내 알림 표시 (토스트 메시지)
     */
    function showPageNotification(message) {
        // Bootstrap 토스트를 사용하여 페이지 내 알림 표시
        const toastHtml = `
            <div class="toast align-items-center text-white bg-primary border-0" role="alert" aria-live="assertive" aria-atomic="true" style="position: fixed; top: 20px; right: 20px; z-index: 9999;">
                <div class="d-flex">
                    <div class="toast-body">
                        ${message}
                    </div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
                </div>
            </div>
        `;

        const toastContainer = document.getElementById('toast-container') || createToastContainer();
        toastContainer.insertAdjacentHTML('beforeend', toastHtml);

        const toastElement = toastContainer.lastElementChild;
        const toast = new bootstrap.Toast(toastElement, {
            autohide: true,
            delay: 5000
        });
        toast.show();

        // 토스트가 닫힌 후 DOM에서 제거
        toastElement.addEventListener('hidden.bs.toast', function() {
            toastElement.remove();
        });
    }

    /**
     * 토스트 컨테이너 생성
     */
    function createToastContainer() {
        const container = document.createElement('div');
        container.id = 'toast-container';
        container.style.position = 'fixed';
        container.style.top = '20px';
        container.style.right = '20px';
        container.style.zIndex = '9999';
        document.body.appendChild(container);
        return container;
    }

    /**
     * WebSocket 연결 해제
     */
    function disconnect() {
        if (stompClient !== null) {
            stompClient.disconnect();
            console.log('WebSocket 연결 해제');
        }
    }

    /**
     * 초기화
     */
    function init() {
        // 페이지 로드 시 알림 권한 요청
        requestNotificationPermission();

        // WebSocket 연결
        connect();

        // 페이지 언로드 시 연결 해제
        window.addEventListener('beforeunload', disconnect);
    }

    // DOM 로드 완료 후 초기화
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

    // 전역에서 접근 가능하도록 export (필요시)
    window.notificationService = {
        connect: connect,
        disconnect: disconnect,
        showNotification: showBrowserNotification
    };
})();


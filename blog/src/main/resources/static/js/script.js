function timeAgo(dateStr) {
    const now = new Date();
    const target = new Date(dateStr);
    const diff = (now - target) / 1000; // 초

    if (diff < 60) return "방금 전";
    if (diff < 3600) return Math.floor(diff / 60) + "분 전";
    if (diff < 86400) return Math.floor(diff / 3600) + "시간 전";
    if (diff < 2592000) return Math.floor(diff / 86400) + "일 전";
    if (diff < 31536000) return Math.floor(diff / 2592000) + "개월 전";
    return Math.floor(diff / 31536000) + "년 전";
}
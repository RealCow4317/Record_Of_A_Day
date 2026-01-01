(() => {
    'use strict'

    const storedTheme = localStorage.getItem('theme')

    const getPreferredTheme = () => {
        if (storedTheme && storedTheme !== 'auto') {
            return storedTheme
        }
        return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
    }

    const setTheme = (theme) => {
        if (theme === 'auto') {
            // 시스템 설정 따라 적용
            const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
            document.documentElement.setAttribute('data-bs-theme', prefersDark ? 'dark' : 'light')
        } else {
            document.documentElement.setAttribute('data-bs-theme', theme)
        }
    }

    // 시스템 테마 변경 시 실시간 반응
    window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', e => {
        const savedTheme = localStorage.getItem('theme')
        if (savedTheme === 'auto') {
            document.documentElement.setAttribute('data-bs-theme', e.matches ? 'dark' : 'light')
        }
    })

    // 초기 테마 설정
    setTheme(getPreferredTheme())

    window.addEventListener('DOMContentLoaded', () => {
        const themeButtons = document.querySelectorAll('[data-theme-value]')
        const currentTheme = localStorage.getItem('theme') || 'auto'

        themeButtons.forEach(button => {
            // 현재 테마인 버튼에 active 클래스 추가
            if (button.getAttribute('data-theme-value') === currentTheme) {
                button.classList.add('active')
            }

            button.addEventListener('click', () => {
                const theme = button.getAttribute('data-theme-value')
                localStorage.setItem('theme', theme)
                setTheme(theme)

                themeButtons.forEach(btn => btn.classList.remove('active'))
                button.classList.add('active')
            })
        })
    })
})()

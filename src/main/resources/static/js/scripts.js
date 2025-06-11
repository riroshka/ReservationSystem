document.addEventListener('DOMContentLoaded', function() {
    // Активация пунктов меню
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.nav-menu a');

    navLinks.forEach(link => {
        if (link.getAttribute('href') === currentPath) {
            link.classList.add('active');
        }
    });

    // Динамическое скрытие элементов по ролям
    const roleElements = document.querySelectorAll('[sec\\:authorize]');
    roleElements.forEach(el => {
        const attr = el.getAttribute('sec:authorize');
        const requiredRoles = parseRolesFromAttribute(attr);

        if (!userHasRequiredRoles(requiredRoles)) {
            el.style.display = 'none';
        }
    });

    // Плавная прокрутка для якорей
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                window.scrollTo({
                    top: target.offsetTop - 80,
                    behavior: 'smooth'
                });
            }
        });
    });

    // Анимация карточек при появлении
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('animate-in');
            }
        });
    }, { threshold: 0.1 });

    document.querySelectorAll('.card').forEach(card => {
        observer.observe(card);
    });
});

function parseRolesFromAttribute(attr) {
    if (!attr) return [];

    // Пример: "hasRole('ADMIN')" -> ['ADMIN']
    const matches = attr.match(/hasRole\('(\w+)'\)|hasAnyRole\('([\w, ]+)'\)/);
    if (!matches) return [];

    if (matches[1]) {
        return [matches[1]];
    } else if (matches[2]) {
        return matches[2].split(',').map(r => r.trim());
    }

    return [];
}

function userHasRequiredRoles(requiredRoles) {
    // В реальном приложении эти данные получаются с сервера
    // Здесь упрощенная демонстрационная версия
    const userRoles = getUserRoles(); // Эта функция должна быть реализована на сервере

    return requiredRoles.some(role => userRoles.includes(role));
}

// Заглушка - в реальном приложении данные о ролях приходят с сервера
function getUserRoles() {
    // В реальном приложении это должно приходить из Thymeleaf
    // Пример: const roles = /*[[${#authentication.principal.authorities}]]*/ [];
    return [];
}
function toggleDescription(button) {
    const targetId = button.getAttribute('data-target');
    const description = document.querySelector(targetId);

    // Переключаем видимость описания
    if (description.classList.contains('show')) {
        description.classList.remove('show');
        button.textContent = 'Подробнее';  // Меняем текст кнопки на "Подробнее"
    } else {
        description.classList.add('show');
        button.textContent = 'Свернуть';  // Меняем текст кнопки на "Свернуть"
    }
}




# PROVse - Freight Work and Handyman Services in Lviv
# PROVse - Вантажні роботи та різноробочі послуги у Львові

Informational website about freight work and handyman services in Lviv. The site provides detailed information about available services and pricing.

Інформаційний веб-сайт про вантажні роботи та різноробочі послуги у Львові. Сайт надає детальну інформацію про доступні послуги та ціни.

## 🚀 Technologies / Технології

- Next.js 14
- TypeScript
- Tailwind CSS
- Framer Motion

## 📋 Prerequisites / Передумови

- Node.js (recommended version 20.x.x LTS / рекомендована версія 20.x.x LTS)
- npm or/або yarn

## 🛠 Project Setup / Налаштування проекту

1. Clone the repository / Склонуйте репозиторій:
```bash
git clone https://github.com/Nazarii-Voitkiv/PROVse-web-site.git
cd PROVse-web-site
```

2. Install dependencies / Встановіть залежності:
```bash
npm install
# or/або
yarn install
```

3. Create `.env` file in the project root with the following variables / Створіть файл `.env` в корені проекту з наступними змінними:
```env
NEXT_PUBLIC_PHONE_NUMBER="your-phone-number"
NEXT_PUBLIC_PHONE_DISPLAY="your-display-phone-number"
```

4. Start the development server / Запустіть проект в режимі розробки:
```bash
npm run dev
# or/або
yarn dev
```

## 📁 Project Structure / Структура проекту

```
frontend/
├── src/
│   ├── app/
│   │   ├── layout.tsx          # Main layout with ToastProvider / Головний layout з ToastProvider
│   │   └── page.tsx            # Main page / Головна сторінка
│   ├── components/
│   │   ├── HeroSection.tsx     # Hero section / Головна секція
│   │   ├── Navbar.tsx          # Navigation bar / Навігаційна панель
│   │   ├── ServicesSection.tsx # Services section / Секція послуг
│   │   └── Toast.tsx           # Notifications component / Компонент сповіщень
│   └── styles/
│       └── globals.css         # Global styles / Глобальні стилі
└── .env                        # Local environment variables / Локальні змінні середовища
```

## 🎨 Features / Особливості

- Responsive design / Адаптивний дизайн
- Modern UI with animations / Сучасний інтерфейс з анімаціями
- Service information display / Відображення інформації про послуги
- Mobile-friendly navigation / Зручна навігація на мобільних пристроях
- Smooth scrolling / Плавна прокрутка
- Direct phone calls / Прямі телефонні дзвінки

## 🌐 Deployment / Деплой

The project can be deployed on Vercel / Проект можна розгорнути на Vercel:

[![Deploy with Vercel](https://vercel.com/button)](https://vercel.com/new/clone?repository-url=https%3A%2F%2Fgithub.com%2FNazarii-Voitkiv%2FPROVse-web-site)

## 📝 License / Ліцензія

This is a proprietary project created as a custom order. All rights reserved. The code cannot be used, copied, or distributed without explicit permission.

Це власницький проект, створений на замовлення. Всі права захищені. Код не може бути використаний, скопійований або розповсюджений без явного дозволу.

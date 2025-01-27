'use client';

import { useState, useEffect } from 'react';
import { FaBars, FaTimes } from 'react-icons/fa';
import { motion, AnimatePresence } from 'framer-motion';

const Navbar = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [isScrolled, setIsScrolled] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setIsScrolled(window.scrollY > 20);
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const menuItems = [
    { name: 'Послуги', id: 'services' },
    { name: 'Про нас', id: 'why-us' },
    { name: 'Контакти', id: 'footer' }
  ];

  const scrollToSection = (id: string) => {
    setIsMenuOpen(false);
    const element = document.getElementById(id);
    if (element) {
      const navbarHeight = 80; // висота навбару
      const elementPosition = element.getBoundingClientRect().top;
      const offsetPosition = elementPosition + window.pageYOffset - navbarHeight;

      window.scrollTo({
        top: offsetPosition,
        behavior: 'smooth'
      });
    }
  };

  const scrollToTop = () => {
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    });
  };

  const menuVariants = {
    closed: {
      opacity: 0,
      x: "100%",
      transition: {
        duration: 0.2
      }
    },
    open: {
      opacity: 1,
      x: "0%",
      transition: {
        duration: 0.3,
        staggerChildren: 0.1,
        when: "beforeChildren"
      }
    }
  };

  const itemVariants = {
    closed: { opacity: 0, x: 20 },
    open: { opacity: 1, x: 0 }
  };

  return (
    <>
      <nav className={`fixed top-0 left-0 right-0 z-50 transition-all duration-300 ${
        isScrolled ? 'bg-white shadow-lg' : 'bg-white/95'
      }`}>
        <div className="container mx-auto px-4">
          <div className="flex items-center justify-between h-20">
            {/* Логотип */}
            <button onClick={scrollToTop} className="text-2xl font-bold">
              <span className="text-green-500">PRO</span>
              <span className="text-blue-500">Vse</span>
              <span className="text-blue-500 ml-2 text-xl font-semibold">Syla</span>
            </button>

            {/* Основне меню - десктоп */}
            <div className="hidden xl:flex items-center space-x-10">
              {menuItems.map((item) => (
                <button
                  key={item.name}
                  onClick={() => scrollToSection(item.id)}
                  className="text-gray-600 hover:text-blue-600 transition-colors"
                >
                  {item.name}
                </button>
              ))}
            </div>

            {/* Контакти та CTA - десктоп */}
            <div className="hidden xl:flex items-center space-x-8">
              <a 
                href={`tel:${process.env.NEXT_PUBLIC_PHONE_NUMBER}`}
                className="text-gray-600 hover:text-blue-600 font-medium tracking-wide 
                            transition-colors duration-200"
              >
                {process.env.NEXT_PUBLIC_PHONE_DISPLAY}
              </a>
              <a 
                href={`tel:${process.env.NEXT_PUBLIC_PHONE_NUMBER}`}
                className="bg-green-500 text-white px-6 py-2.5 rounded-lg font-semibold 
                           hover:bg-green-600 transition-all duration-200 shadow-sm 
                           hover:shadow-md transform hover:-translate-y-0.5"
              >
                Зателефонувати
              </a>
            </div>

            {/* Бургер меню - мобільний */}
            <button
              className="xl:hidden text-gray-600 p-2 focus:outline-none"
              onClick={() => setIsMenuOpen(!isMenuOpen)}
            >
              {isMenuOpen ? <FaTimes size={24} /> : <FaBars size={24} />}
            </button>
          </div>
        </div>

        {/* Мобільне меню */}
        <AnimatePresence>
          {isMenuOpen && (
            <motion.div
              initial="closed"
              animate="open"
              exit="closed"
              variants={menuVariants}
              className="fixed top-20 left-0 right-0 bottom-0 bg-white xl:hidden"
            >
              <div className="container mx-auto px-4 py-6">
                <div className="flex flex-col space-y-6">
                  {menuItems.map((item) => (
                    <motion.div
                      key={item.name}
                      variants={itemVariants}
                      className="border-b border-gray-100 pb-4"
                    >
                      <button
                        onClick={() => scrollToSection(item.id)}
                        className="text-gray-600 hover:text-blue-600 text-lg transition-colors"
                      >
                        {item.name}
                      </button>
                    </motion.div>
                  ))}
                  
                  <motion.div variants={itemVariants} className="pt-4">
                    <a
                      href={`tel:${process.env.NEXT_PUBLIC_PHONE_NUMBER}`}
                      className="block text-gray-600 hover:text-blue-600 text-lg mb-6 transition-colors"
                    >
                      {process.env.NEXT_PUBLIC_PHONE_DISPLAY}
                    </a>
                    <a
                      href={`tel:${process.env.NEXT_PUBLIC_PHONE_NUMBER}`}
                      className="block w-full bg-green-500 text-white px-6 py-3 rounded-lg font-semibold 
                               hover:bg-green-600 transition-all duration-200 text-center"
                    >
                      Зателефонувати
                    </a>
                  </motion.div>
                </div>
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </nav>
    </>
  );
};

export default Navbar;

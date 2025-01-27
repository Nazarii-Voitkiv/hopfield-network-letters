'use client';

import Image from 'next/image';
import { useState, useEffect } from 'react';
import { motion } from 'framer-motion';

const images = [
  '/images/hero/hero-bg.jpg',
  '/images/hero/hero-bg-2.jpg',
  '/images/hero/hero-bg-3.png'
];

const HeroSection = () => {
  const [currentImageIndex, setCurrentImageIndex] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentImageIndex((prevIndex) => (prevIndex + 1) % images.length);
    }, 5000);

    return () => clearInterval(interval);
  }, []);

  return (
    <>
      <section id="hero" className="relative min-h-[calc(100vh-5rem)] flex items-center overflow-hidden">
        {/* Фонові зображення з анімацією */}
        <div className="fixed top-0 left-0 w-full h-full -z-10">
          {images.map((image, index) => (
            <div
              key={image}
              className="absolute inset-0 transition-opacity duration-1000"
              style={{
                opacity: index === currentImageIndex ? 1 : 0
              }}
            >
              <Image
                src={image}
                alt={`Фонове зображення ${index + 1}`}
                fill
                className="object-cover"
                priority={index === 0}
              />
              {/* Градієнтний оверлей */}
              <div className="absolute inset-0" 
                   style={{
                     background: 'linear-gradient(to bottom, rgba(0,0,0,0.1) 0%, rgba(0,0,0,0.5) 50%, rgba(0,0,0,0.7) 100%)',
                   }} 
              />
            </div>
          ))}
        </div>

        {/* Контент */}
        <div className="relative z-10 container mx-auto px-4 py-12">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8 }}
            className="max-w-3xl"
          >
            <h1 className="text-4xl lg:text-5xl font-bold mb-6 leading-tight text-white opacity-90">
              Вантажні роботи та різноробочі послуги у Львові
            </h1>
            <p className="text-xl mb-8 text-gray-200 opacity-85">
              Переїзди, демонтаж, вивіз сміття та інші фізичні роботи
            </p>
          </motion.div>
        </div>
      </section>
    </>
  );
};

export default HeroSection;

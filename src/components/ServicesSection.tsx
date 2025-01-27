'use client';

import { FaTruck, FaTools, FaHammer, FaTrash, FaTruckLoading } from 'react-icons/fa';
import { motion } from 'framer-motion';
import { useInView } from 'framer-motion';
import { useRef } from 'react';

const services = [
  {
    title: 'Вантажні роботи',
    description: 'Професійна допомога у завантаженні, розвантаженні та транспортуванні вантажів будь-якої складності.',
    icon: FaTruckLoading,
    price: 'від 500 грн',
    minDescription: 'Мінімальне замовлення 2 години',
    features: [
      'Вантажники від 2-х осіб',
      'Підйом на поверх',
      'Розбирання меблів'
    ]
  },
  {
    title: 'Різноробочі послуги',
    description: 'Виконання різноманітних фізичних робіт: прибирання території, копання, будівельні роботи тощо.',
    icon: FaTools,
    price: 'від 400 грн',
    minDescription: 'Мінімальне замовлення 3 години',
    features: [
      'Досвідчені працівники',
      'Власний інструмент',
      'Прибирання території'
    ]
  },
  {
    title: 'Переїзди',
    description: 'Організація та здійснення квартирних, офісних та інших видів переїздів "під ключ".',
    icon: FaTruck,
    price: 'від 1000 грн',
    minDescription: 'Мінімальне замовлення 3 години',
    features: [
      'Вантажний транспорт',
      'Пакувальні матеріали',
      'Монтаж/демонтаж меблів'
    ]
  },
  {
    title: 'Демонтажні роботи',
    description: 'Професійний демонтаж конструкцій, стін, підлоги та інших елементів з подальшим прибиранням.',
    icon: FaHammer,
    price: 'від 800 грн',
    minDescription: 'Мінімальне замовлення 4 години',
    features: [
      'Демонтаж будь-якої складності',
      'Вивіз сміття',
      'Прибирання території'
    ]
  },
  {
    title: 'Вивіз сміття',
    description: 'Швидкий та екологічний вивіз будівельного та побутового сміття з подальшою його утилізацією.',
    icon: FaTrash,
    price: 'від 600 грн',
    minDescription: 'Мінімальне замовлення 2 години',
    features: [
      'Вантажний транспорт',
      'Завантаження сміття',
      'Утилізація'
    ]
  }
];

const ServicesSection = () => {
  const ref = useRef(null);
  const isInView = useInView(ref, { 
    once: true, 
    margin: "-66% 0px -33% 0px"
  });

  const containerVariants = {
    hidden: { 
      opacity: 0,
      y: 50,
      WebkitTransform: 'translate3d(0, 50px, 0)',
      transform: 'translate3d(0, 50px, 0)'
    },
    visible: {
      opacity: 1,
      y: 0,
      WebkitTransform: 'translate3d(0, 0, 0)',
      transform: 'translate3d(0, 0, 0)',
      transition: {
        duration: 0.6,
        ease: [0.25, 0.1, 0.25, 1],
        staggerChildren: 0.1
      }
    }
  };

  const itemVariants = {
    hidden: { 
      opacity: 0,
      y: 20,
      WebkitTransform: 'translate3d(0, 20px, 0)',
      transform: 'translate3d(0, 20px, 0)'
    },
    visible: {
      opacity: 1,
      y: 0,
      WebkitTransform: 'translate3d(0, 0, 0)',
      transform: 'translate3d(0, 0, 0)',
      transition: {
        duration: 0.5,
        ease: [0.25, 0.1, 0.25, 1]
      }
    }
  };

  return (
    <>
      <section id="services" className="relative min-h-screen flex items-center py-20 bg-white" ref={ref}>
        {/* Контент */}
        <motion.div 
          className="container mx-auto px-4"
          initial="hidden"
          animate={isInView ? "visible" : "hidden"}
          variants={containerVariants}
        >
          <motion.h2 
            className="text-4xl font-bold text-gray-800 text-center mb-12"
            variants={itemVariants}
          >
            Наші послуги
          </motion.h2>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5 gap-8">
            {services.map((service) => {
              const Icon = service.icon;
              return (
                <motion.div
                  key={service.title}
                  className="bg-white rounded-xl p-6 shadow-lg
                             transform transition-all duration-300 hover:scale-105
                             hover:shadow-xl group"
                  variants={itemVariants}
                >
                  <div className="flex flex-col items-center text-center h-full">
                    <div className="text-green-500 mb-4 transform transition-transform 
                                duration-300 group-hover:scale-110">
                      <Icon size={48} />
                    </div>
                    <h3 className="text-xl font-semibold text-gray-800 mb-2">
                      {service.title}
                    </h3>
                    <p className="text-gray-600 mb-4 flex-grow">
                      {service.description}
                    </p>
                    <div className="w-full border-t border-gray-200 my-4"></div>
                    <ul className="text-sm text-gray-600 mb-4 space-y-2">
                      {service.features.map((feature, idx) => (
                        <li key={idx} className="flex items-center justify-center">
                          <span className="w-1.5 h-1.5 bg-green-500 rounded-full mr-2"></span>
                          {feature}
                        </li>
                      ))}
                    </ul>
                    <div className="text-green-600 font-bold mb-1">
                      {service.price}
                    </div>
                    <div className="text-sm text-green-600 italic mb-2">
                      Ціна договірна
                    </div>
                    <div className="text-sm text-gray-500 mb-4">
                      {service.minDescription}
                    </div>
                  </div>
                </motion.div>
              );
            })}
          </div>
        </motion.div>
      </section>
    </>
  );
};

export default ServicesSection;

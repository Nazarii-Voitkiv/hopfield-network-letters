'use client';

import { motion } from 'framer-motion';
import { useInView } from 'framer-motion';
import { useRef } from 'react';
import { FaClock, FaAward, FaShieldAlt, FaMoneyBillWave } from 'react-icons/fa';

const advantages = [
  {
    icon: FaAward,
    title: 'Досвід понад 5 років',
    description: 'Тисячі успішно виконаних замовлень та задоволених клієнтів по всій Україні'
  },
  {
    icon: FaClock,
    title: 'Швидкість виконання',
    description: 'Оперативно приїжджаємо та виконуємо роботу в обумовлені терміни'
  },
  {
    icon: FaShieldAlt,
    title: 'Відповідальність',
    description: 'Гарантуємо безпеку ваших речей та якісне виконання робіт'
  },
  {
    icon: FaMoneyBillWave,
    title: 'Адекватні ціни',
    description: 'Прозоре ціноутворення без прихованих платежів'
  }
];

const WhyUsSection = () => {
  const ref = useRef(null);
  const isInView = useInView(ref, { once: true, margin: "-66% 0px -33% 0px" });

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
      x: -50,
      WebkitTransform: 'translate3d(-50px, 0, 0)',
      transform: 'translate3d(-50px, 0, 0)'
    },
    visible: {
      opacity: 1,
      x: 0,
      WebkitTransform: 'translate3d(0, 0, 0)',
      transform: 'translate3d(0, 0, 0)',
      transition: {
        duration: 0.5,
        ease: [0.25, 0.1, 0.25, 1]
      }
    }
  };

  return (
    <section 
      id="why-us"
      ref={ref}
      className="relative min-h-screen flex items-center py-32"
      style={{
        background: `
          linear-gradient(to right, rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.9)),
          url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23f0f0f0' fill-opacity='0.4'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")
        `
      }}
    >
      <motion.div 
        className="container mx-auto px-4"
        variants={containerVariants}
        initial="hidden"
        animate={isInView ? "visible" : "hidden"}
      >
        <motion.div variants={itemVariants} className="mb-20">
          <h2 className="text-4xl lg:text-5xl font-bold text-gray-800 text-center mb-6">
            Чому клієнти обирають нас?
          </h2>
          <p className="text-gray-600 text-center mb-16 max-w-2xl mx-auto text-lg">
            Наша команда працює для вас вже більше 5 років, і ми знаємо, що важливо для наших клієнтів
          </p>
        </motion.div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-12">
          {advantages.map((item) => {
            const Icon = item.icon;
            return (
              <motion.div
                key={item.title}
                variants={itemVariants}
                className="bg-white rounded-xl p-8 shadow-lg hover:shadow-xl 
                         transition-all duration-300 transform hover:-translate-y-1"
              >
                <div className="flex flex-col items-center text-center">
                  <div className="text-green-500 mb-6">
                    <Icon size={48} />
                  </div>
                  <h3 className="text-2xl font-semibold text-gray-800 mb-4">
                    {item.title}
                  </h3>
                  <p className="text-gray-600 text-lg">
                    {item.description}
                  </p>
                </div>
              </motion.div>
            );
          })}
        </div>
      </motion.div>
    </section>
  );
};

export default WhyUsSection;

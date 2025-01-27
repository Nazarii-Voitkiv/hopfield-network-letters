'use client';

import { motion } from 'framer-motion';
import { useInView } from 'framer-motion';
import { useRef } from 'react';
import { FaPhone, FaMapMarkerAlt } from 'react-icons/fa';

const Footer = () => {
  const ref = useRef(null);
  const isInView = useInView(ref, { once: true, margin: "-100px" });

  const containerVariants = {
    hidden: { opacity: 0, y: 20 },
    visible: {
      opacity: 1,
      y: 0,
      transition: {
        duration: 0.5,
        staggerChildren: 0.2
      }
    }
  };

  const itemVariants = {
    hidden: { opacity: 0, y: 20 },
    visible: {
      opacity: 1,
      y: 0,
      transition: { duration: 0.5 }
    }
  };

  return (
    <footer id="footer" ref={ref} className="bg-gray-900 text-white py-16">
      <motion.div
        className="container mx-auto px-4"
        variants={containerVariants}
        initial="hidden"
        animate={isInView ? "visible" : "hidden"}
      >
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-12">
          {/* Контактна інформація */}
          <motion.div variants={itemVariants} className="space-y-8">
            <h2 className="text-3xl font-bold mb-6">Контакти</h2>
            
            <div className="flex items-start space-x-4">
              <FaMapMarkerAlt className="text-green-500 text-xl mt-1" />
              <div>
                <h3 className="font-semibold mb-2">Адреса</h3>
                <p className="text-gray-300">м. Львів, Львівська область, Україна, 79000</p>
              </div>
            </div>

            <div className="flex items-start space-x-4">
              <FaPhone className="text-green-500 text-xl mt-1" />
              <div>
                <h3 className="font-semibold mb-2">Телефон</h3>
                <a 
                  href={`tel:${process.env.NEXT_PUBLIC_PHONE_NUMBER}`}
                  className="text-gray-300 hover:text-green-500 transition-colors"
                >
                  {process.env.NEXT_PUBLIC_PHONE_DISPLAY}
                </a>
              </div>
            </div>
          </motion.div>

          {/* Google Maps */}
          <motion.div variants={itemVariants} className="w-full h-[450px] rounded-xl overflow-hidden shadow-xl">
            <iframe 
              src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d82352.68481526324!2d23.92983584552284!3d49.8326598448169!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x473add7c09109a57%3A0x4223c517012378e2!2z0JvRjNCy0ZbQsiwg0JvRjNCy0ZbQstGB0YzQutCwINC-0LHQu9Cw0YHRgtGMLCDQo9C60YDQsNGX0L3QsCwgNzkwMDA!5e0!3m2!1suk!2spl!4v1737554626001!5m2!1suk!2spl"
              width="100%"
              height="100%"
              style={{ border: 0 }}
              allowFullScreen
              loading="lazy"
              referrerPolicy="no-referrer-when-downgrade"
            />
          </motion.div>
        </div>

        {/* Copyright */}
        <motion.div 
          variants={itemVariants}
          className="text-center mt-12 pt-8 border-t border-gray-800"
        >
          <p className="text-gray-400">
            {new Date().getFullYear()} PROVse. Всі права захищені.
          </p>
        </motion.div>
      </motion.div>
    </footer>
  );
};

export default Footer;

/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        'azul-clinico': '#0f4c81',     // Color corporativo base
        'verde-esmeralda': '#10b981',  // Color para acciones de éxito
        'gris-fondo': '#f3f4f6'        // Fondo para reducir fatiga visual
      }
    },
  },
  plugins: [],

}
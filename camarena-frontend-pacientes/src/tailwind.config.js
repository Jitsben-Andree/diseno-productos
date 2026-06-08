/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        'azul-clinico': '#0f4c81',     // Marca
        'verde-esmeralda': '#10b981',  // Éxito / Resultados listos
        'fondo-app': '#f8fafc',        // Fondo muy claro para alto contraste
        'texto-principal': '#1e293b'   // Gris muy oscuro para legibilidad (Adultos mayores)
      }
    },
  },
  plugins: [],
  corePlugins: {
    preflight: false,
  }
}

/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        lumina: {
          primary: "#009488",     // Teal green
          secondary: "#0F172A",   // Dark navy
          tertiary: "#F8FAFC",    // Off-white main background
          neutral: "#64748B",     // Slate gray text/borders
          dark: "#020617",
          light: "#FFFFFF"
        }
      }
    },
  },
  plugins: [],
}

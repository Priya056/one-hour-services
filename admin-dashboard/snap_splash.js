import { execSync } from 'child_process';

const targetHtml = 'file:///C:/Users/priya/.gemini/antigravity/brain/ac3536f7-f47d-4081-8a76-1b3c31d07514/splash_preview.html';
const targetPng = 'C:\\Users\\priya\\.gemini\\antigravity\\brain\\ac3536f7-f47d-4081-8a76-1b3c31d07514\\splash_screen_updated.png';
const edgePath = 'C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe';

console.log('Rendering Updated Splash Screen Image...');
execSync(`"${edgePath}" --headless --disable-gpu --window-size=900,950 --screenshot="${targetPng}" "${targetHtml}"`);
console.log('Updated Splash Screen Image Saved Successfully!');

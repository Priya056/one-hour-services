import { execSync } from 'child_process';

const targetHtml = 'file:///C:/Users/priya/.gemini/antigravity/brain/ac3536f7-f47d-4081-8a76-1b3c31d07514/android_ui_showcase.html';
const targetPng = 'C:\\Users\\priya\\.gemini\\antigravity\\brain\\ac3536f7-f47d-4081-8a76-1b3c31d07514\\android_ui_design_prototypes.png';
const edgePath = 'C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe';

console.log('Rendering Android UI Design Prototypes Image...');
execSync(`"${edgePath}" --headless --disable-gpu --window-size=1440,1600 --screenshot="${targetPng}" "${targetHtml}"`);
console.log('Android UI Design Prototypes Image Saved Successfully!');

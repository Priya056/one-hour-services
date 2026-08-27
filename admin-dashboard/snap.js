import { execSync } from 'child_process';
import fs from 'fs';
import path from 'path';

const targetPath = 'C:\\Users\\priya\\.gemini\\antigravity\\brain\\ac3536f7-f47d-4081-8a76-1b3c31d07514\\dashboard_screen1.png';
const edgePath = 'C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe';

console.log('Capturing screenshot...');
execSync(`"${edgePath}" --headless --disable-gpu --window-size=1440,900 --screenshot="${targetPath}" http://localhost:5173/`);
console.log('Done!');

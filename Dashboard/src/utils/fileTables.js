const fs = require('fs').promises;

let listeners = {};
let data = {};
let cycleStarted = false;


export function addKeyListener(key, callback) {
    if (!cycleStarted) {
        cycleStarted = true;
        mainCycle();
    }
    listeners[key] = callback;
}


export function putValue(key, value) {
    return;
}

function initData(filePath) {
    console.log('initData', filePath);
    fs.readFile(filePath)
        .then(data => {
            console.log('data', data);
            let lines = data.split('\n');
            console.log('lines', lines);
        })
        .catch(err => console.log('ERROR', err))
}

initData('./dataFile.csv');

function mainCycle() {



    setTimeout(mainCycle, 1000);
}
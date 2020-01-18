import Vue from 'vue'
import App from './App.vue'
import vuetify from './plugins/vuetify';
import 'roboto-fontface/css/roboto/roboto-fontface.css'
import '@mdi/font/css/materialdesignicons.css'

import { NetworkTables } from './utils/networktables'
import * as logger from './utils/logger'

Vue.config.productionTip = false
// IP address of the robot
const IP_ADDRESS = '10.29.90.2';
const retryFreq = 10; //In seconds
let isRobotConnected = false;


new Vue({
    vuetify,
    render: h => h(App),
    mounted() {
        logger.logEvent('MOUNTED');
        NetworkTables.addRobotConnectionListener(onRobotConnection, false);
        isRobotConnected = NetworkTables.isRobotConnected();
        if (!isRobotConnected) {
            setTimeout(this.connectionRetry, 1000);
        }
        NetworkTables.addKeyListener('/SmartDashboard/UltrasonicDown', consoleListener);
    },
    methods: {
        connectionRetry: function () {
            // If the robot is connected do nothing
            if (isRobotConnected) return;

            // If no connection, try to connect again and then check again in 1 second
            logger.logEvent('Attepting robot connection to: ' + IP_ADDRESS);
            NetworkTables.connect(IP_ADDRESS);
            setTimeout(this.connectionRetry, retryFreq * 1000);

        }
    }
}).$mount('#app')


function onRobotConnection() {
    logger.logEvent('Robot has connected to dashboard');
}
function consoleListener(key, value) {
    logger.logEvent(key + ' :: ' + value);
}
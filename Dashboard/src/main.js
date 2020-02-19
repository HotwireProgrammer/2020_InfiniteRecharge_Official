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


// import Vue from 'vue';
import zingchartVue from 'zingchart-vue';

Vue.component('zingchart', zingchartVue)

new Vue({
    vuetify,
    render: h => h(App),
    mounted() {
        logger.logEvent('MOUNTED');
        NetworkTables.addRobotConnectionListener(onRobotConnection, false);
        if (!NetworkTables.isRobotConnected()) {
            setTimeout(this.connectionRetry, 1000);
        }
        this.initDataLogging();
    },
    methods: {
        connectionRetry: function () {
            // If the robot is connected do nothing
            if (NetworkTables.isRobotConnected()) return;

            // If no connection, try to connect again and then check again in 1 second
            logger.logEvent('Attepting robot connection to: ' + IP_ADDRESS);
            NetworkTables.connect(IP_ADDRESS);
            setTimeout(this.connectionRetry, retryFreq * 1000);
        },
        initDataLogging: function() {
            if (this.dataLoggingStarted) return;

            this.dataLoggingStarted = true;
            NetworkTables.addKeyListener('/SmartDashboard/ballCounter', logger.logData);
            NetworkTables.addKeyListener('/SmartDashboard/PDP_Temperature', logger.logData);
            NetworkTables.addKeyListener('/SmartDashboard/PDP_Voltage', logger.logData);
            NetworkTables.addKeyListener('/SmartDashboard/Shooter_Speed', logger.logData);
            NetworkTables.addKeyListener('/SmartDashboard/Shooter_RPM', logger.logData);

            for (let i = 0; i < 15; i++) {
                NetworkTables.addKeyListener('/SmartDashboard/PDP_' + i, logger.logData);
            }

            NetworkTables.addKeyListener('/SmartDashboard/RobotEnabled', (enabled) => {
                if (!enabled) {
                    endMatchProcessing();
                }
            });
        }
    }
}).$mount('#app')


function onRobotConnection() {
    logger.logEvent('Robot has connected to dashboard');
}
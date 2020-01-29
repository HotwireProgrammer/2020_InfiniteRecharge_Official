<script>
import { NetworkTables } from '../utils/networktables';
import * as logger from '../utils/logger';
import Trend from "vuetrend"

const SERIES_LENGTH = 400;

export default {
    name: 'PIDChart',
    components: {
        Trend
    },

    mounted: function() {
        NetworkTables.putValue('/SmartDashboard/Shooter_RPMTarget', 0);
        NetworkTables.addKeyListener('/SmartDashboard/Shooter_RPM', this.addSpeedvalue);
        this.pVal = NetworkTables.getValue('/SmartDashboard/Shooter_P', 0)
        this.iVal = NetworkTables.getValue('/SmartDashboard/Shooter_I', 0)
        this.dVal = NetworkTables.getValue('/SmartDashboard/Shooter_D', 0)
        logger.logData('/SmartDashboard/Shooter_P', this.pVal);
        logger.logData('/SmartDashboard/Shooter_I', this.iVal);
        logger.logData('/SmartDashboard/Shooter_D', this.dVal);
    },
    methods: {
        addSpeedvalue: function(key, newSpeed) {
            let time = new Date();
            this.rpmData.push(newSpeed)
            if (this.rpmData.length > SERIES_LENGTH) {
                this.rpmData.shift();
            }
            logger.logData(key, newSpeed);
        },
        send: function() {
            NetworkTables.putValue('/SmartDashboard/Shooter_P', this.pVal);
            NetworkTables.putValue('/SmartDashboard/Shooter_I', this.iVal);
            NetworkTables.putValue('/SmartDashboard/Shooter_D', this.dVal);
            NetworkTables.putValue('/SmartDashboard/Shooter_RPMTarget', this.rpmGoalVal);
            logger.logData('/SmartDashboard/Shooter_P', this.pVal);
            logger.logData('/SmartDashboard/Shooter_I', this.iVal);
            logger.logData('/SmartDashboard/Shooter_D', this.dVal);
            logger.logData('/SmartDashboard/Shooter_RPMTarget', this.rpmGoalVal);
        }
    },
    data: function() {
        return {
            pVal: 0,
            iVal: 0,
            dVal: 0,
            rpmGoalVal: 0,
            rpmData: [],
        }
    }
};

</script>

<template>
    <div>
        <trend
            :data="rpmData"
            auto-draw
            :gradient="['#ff3d00']"
        >
        </trend>
        <v-row no-gutters>
            <v-col cols="1">
                <v-text-field label="P" v-model="pVal"></v-text-field>
            </v-col>
            <v-col cols="1">
                <v-text-field label="I" v-model="iVal"></v-text-field>
            </v-col>
            <v-col cols="1">
                <v-text-field label="D" v-model="dVal"></v-text-field>
            </v-col>
             <v-col cols="4">
                <v-text-field label="PRM Goal" v-model="rpmGoalVal"></v-text-field>
            </v-col>
             <v-col cols="4">
                <v-btn outlined color="primary" @click="send()">Send</v-btn>
            </v-col>
        </v-row>
    </div>
</template>
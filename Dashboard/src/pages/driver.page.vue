<script>
import { NetworkTables } from '../utils/networktables'
import MatchTimer from '../components/MatchTimer'
import Indicator from '../components/Indicator'
import BallCounter from '../components/BallCounter'

import * as logger from '../utils/logger'


export default {
    name: 'DriverPage',

    components: {
        Indicator,
        BallCounter,
        MatchTimer
    },

    data: function() {
        return {
            autoModeValue: 0,
            colorSelectionValue: 0,
            shootingWheelSpeed: 0,
            brownedOut: false,
            isOverheating: false,
            isAmpWarning: false,
            autoModes: [
                {
                    text: 'Most Basic Shoot',
                    value: 0
                },{
                    text: 'Trench Five Ball',
                    value: 1
                },{
                    text: 'Right Eight Ball',
                    value: 2
                },{
                    text: 'Center Five Ball',
                    value: 3
                },{
                    text: 'Trench Six Ball',
                    value: 4
                }
            ]
        }
    },
    methods: {
        updateAutoModes: function(autoModeValue){
            NetworkTables.putValue("/SmartDashboard/autoMode", autoModeValue);
        },

        getImgSrc: function() {
            return require(`../assets/field-${this.autoModeValue}.jpg`);
        },

        checkMotors: function() {
            let temp = [];
            let amps = [];
            temp.push(NetworkTables.addKeyListener('/SmartDashboard/DriveTrain_LeftOne', logger.logData));
            temp.push(NetworkTables.addKeyListener('/SmartDashboard/DriveTrain_LeftTwo', logger.logData));
            temp.push(NetworkTables.addKeyListener('/SmartDashboard/DriveTrain_RightOne', logger.logData));
            temp.push(NetworkTables.addKeyListener('/SmartDashboard/DriveTrain_RightTwo', logger.logData));
            this.isOverheating = temp[0] > 100 || temp[1] > 100 || temp[2] > 100 || temp[3] > 100;
            this.isAmpWarning = false;
            for (let i = 0; i < 15; i++) {
                this.isAmpWarning = NetworkTables.getValue('/SmartDashboard/PDP_' + i, 0) > 60 || this.isAmpWarning;
            }
        }
    },
    mounted: function() {
        NetworkTables.addRobotConnectionListener(
            (connected) => {
                console.log('robot connected', )
                return this.robotConnected = connected
            },
            true
        );

        NetworkTables.addKeyListener(
            '/SmartDashboard/PDP_Voltage',
            (k, val) => {
                if (val < 7) {
                    this.brownedOut = true;
                } else {
                    this.brownedOut = false;
                }
            }
        );

        NetworkTables.addKeyListener(
            "/SmartDashboard/Shooter_Speed",
            (k, val) => {
                this.shootingWheelReady = val > 0;
                this.shootingWheelSpeed = val;
            }
        );
    }
};
</script>

<template>
<v-container class="fill-height" fluid>

    <v-row no-gutters>
        <v-col cols="6">
            <img src="http://10.29.90.11:5800" class="limelight">
        </v-col>
        <v-col cols="3">
            <indicator class="indicator" icon="battery-alert" label="Brown Out" v-bind:toggledValue="this.brownedOut" warn />
            <indicator class="indicator" icon="battery-alert" label="Over Heating" v-bind:toggledValue="this.isOverheating" warn />
            <indicator class="indicator" icon="battery-alert" label="High Amps" v-bind:toggledValue="this.isAmpWarning" warn />
            <v-select
                :items="autoModes"
                label="Auto Mode"
                v-model="autoModeValue"
                outlined
                color="primary"
                item-color="primary"
                class="auto-dropdown"
                @change="updateAutoModes" 
            ></v-select>
            <img class="field-img" v-bind:src="getImgSrc()" />
        </v-col>
        <v-col cols="3">
            <MatchTimer />
            <indicator class="indicator" icon="robot" label="Robot Enabled" networkKey="RobotEnabled" />
            <indicator class="indicator" icon="robot-mower" label="Intake on" networkKey="intakeMotor" />
            <indicator class="indicator" icon="robot-industrial" label="Intake Down" networkKey="intakeExtended" />
            <indicator class="indicator" icon="ship-wheel" label="Spinning Wheel" warn spin networkKey="TODO_KEY" />
            <indicator class="indicator" icon="share-all" label="Shooter Ready" v-bind:toggledValue="shootingWheelSpeed" />
            <BallCounter /> 
        </v-col>
    </v-row>
</v-container>
</template>

<style scoped lang="scss">
    .indicator {
        margin-bottom: 20px;
        font-size: 35px;
    }
    .chart {
        width: 1000px;
    }

    .auto-dropdown {
        display: inline-block;
        width: 285px;
        color: pink;
    }

    .field-img {
        display: inline-block;
        margin-left: 0px;
        margin-bottom: -40px;
        width: 85%;
    }

    .limelight {
        width:95%;
    }

    .match-time {
        font-size: 50px;
    }
</style>
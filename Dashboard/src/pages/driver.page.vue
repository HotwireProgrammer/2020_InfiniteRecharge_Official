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
            const WARNING_TEMP = 30;
            const WARNING_AMPS = 30;
            temp.push(NetworkTables.getValue('/SmartDashboard/DriveTrain_LeftOne', 0));
            temp.push(NetworkTables.getValue('/SmartDashboard/DriveTrain_LeftTwo', 0));
            temp.push(NetworkTables.getValue('/SmartDashboard/DriveTrain_RightOne', 0));
            temp.push(NetworkTables.getValue('/SmartDashboard/DriveTrain_RightTwo', 0));
            this.isOverheating = temp[0] > WARNING_TEMP || temp[1] > WARNING_TEMP || temp[2] > WARNING_TEMP || temp[3] > WARNING_TEMP;
            this.isAmpWarning = false;
            for (let i = 0; i < 15; i++) {
                this.isAmpWarning = NetworkTables.getValue('/SmartDashboard/PDP_' + i, 0) > WARNING_AMPS || this.isAmpWarning;
            }
        }
    },
    mounted: function() {
        setInterval(this.checkMotors, 500);

        NetworkTables.addRobotConnectionListener(
            (connected) => {
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
            <indicator class="indicator" icon="battery-alert-variant" label="Brown Out" v-bind:status="this.brownedOut" warn />
            <indicator class="indicator" icon="thermometer-alert" label="Over Heating" v-bind:status="this.isOverheating" warn />
            <indicator class="indicator" icon="flash-alert" label="High Amps" v-bind:status="this.isAmpWarning" warn />
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
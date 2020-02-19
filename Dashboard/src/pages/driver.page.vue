<script>
import { NetworkTables } from '../utils/networktables'
import Indicator from '../components/Indicator'
import BallCounter from '../components/BallCounter'

import * as logger from '../utils/logger'


export default {
    name: 'DriverPage',

    components: {
        Indicator,
        BallCounter,
    },

    data: function() {
        return {
            autoModeValue: 0,
            colorSelectionValue: 0,
            robotConnected: false,
            matchTime: 0,
            shootingWheelToggled: false,
            shootingWheelTarget: 0,
            shootingWheelSpeed: 0,
            brownedOut: false,
            autoModes: [
                {
                    text: 'Most Basic Shoot',
                    value: 0
                },{
                    text: 'Right Five Ball',
                    value: 1
                },{
                    text: 'Center Eight Ball',
                    value: 2
                },{
                    text: 'EMPTY',
                    value: 3
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
        }
    },
    mounted: function() {
        NetworkTables.addRobotConnectionListener(
            (connected) => this.robotConnected = connected,
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
            "/SmartDashboard/Shooter_RPMTarget",
            (k, val) => {
                this.shootingWheelToggled = val > 0;
                this.shootingWheelTarget = val;
            }
        );

        NetworkTables.addKeyListener(
            "/SmartDashboard/Shooter_Speed",
            (k, val) => {
                this.shootingWheelReady = val > 0;
                this.shootingWheelSpeed = val;
            }
        );
         NetworkTables.addKeyListener(
            "/SmartDashboard/MatchTime",
            (k, val) => {
                this.matchTime = val;
            }
        );
    }
};
</script>

<template>
<v-container class="fill-height" fluid>

    <v-row no-gutters>
        <v-col cols="6">
            <video class="video">
                <source src="http://10.29.90.2:5800" type="video/mp4">
            </video>
        </v-col>
        <v-col cols="3">
            <indicator class="indicator" icon="car-connected" label="Robot Connected" v-bind:toggledValue="this.robotConnected" />
            <indicator class="indicator" icon="robot" label="Robot Enabled" networkKey="RobotEnabled" />
            <indicator class="indicator" icon="robot-mower" label="Intake on" networkKey="intakeMotor" />
            <indicator class="indicator" icon="robot-industrial" label="Intake Down" networkKey="intakeExtended" />
            <indicator class="indicator" icon="battery-alert" label="Brown Out" networkKey="PDP_Voltage" warn />
        </v-col>
        <v-col cols="3">
            <indicator class="indicator" icon="elevator-up" label="Climber Locked" warn networkKey="TODO_KEY" />
            <indicator class="indicator" icon="elevator-up" label="Climbing" networkKey="TODO_KEY" />
            <indicator class="indicator" icon="transfer-up" label="Climber Extending" networkKey="TODO_KEY" />
            <indicator class="indicator" icon="ship-wheel" label="Spinning Wheel" spin networkKey="TODO_KEY" />
            <indicator class="indicator" icon="latitude" label="Shooter Spinning" spin v-bind:toggledValue="shootingWheelToggled" />
            <indicator class="indicator" icon="share-all" label="Shooter Ready" v-bind:toggledValue="shootingWheelSpeed" />
            <BallCounter /> 
        </v-col>
    </v-row>
    <v-row no-gutters>
        <v-col cols="6">
            <div class="choices">
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
            </div>
        </v-col>
        <v-col cols="6">
            <span class="match-time">
                {{matchTime}} Seconds
            </span>
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
        margin-left: 10px;
        margin-bottom: -40px;
    }

    .video {
        width:95%;
    }

    .match-time {
        font-size: 50px;
    }
</style>
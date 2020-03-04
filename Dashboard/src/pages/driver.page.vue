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
            matchTime: 0,
            shootingWheelSpeed: 0,
            brownedOut: false,
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
            <img src="http://10.29.90.11:5800" class="limelight">
        </v-col>
        <v-col cols="3">
            <indicator class="indicator" icon="robot" label="Robot Enabled" networkKey="RobotEnabled" />
            <indicator class="indicator" icon="robot-mower" label="Intake on" networkKey="intakeMotor" />
            <indicator class="indicator" icon="robot-industrial" label="Intake Down" networkKey="intakeExtended" />
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
            <indicator class="indicator" icon="battery-alert" label="Brown Out" v-bind:toggledValue="this.brownedOut" warn />
            <indicator class="indicator" icon="ship-wheel" label="Spinning Wheel" warn spin networkKey="TODO_KEY" />
            <indicator class="indicator" icon="share-all" label="Shooter Ready" v-bind:toggledValue="shootingWheelSpeed" />
            <BallCounter /> 
            <span class="match-time">
                {{matchTime}} Seconds
            </span>
        </v-col>
    </v-row>
    <v-row no-gutters>
        <v-col cols="6">
            <div class="choices">
                
 
            </div>
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

    .limelight {
        width:95%;
    }

    .match-time {
        font-size: 50px;
    }
</style>
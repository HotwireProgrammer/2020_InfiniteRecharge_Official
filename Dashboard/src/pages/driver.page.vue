<script>
import { NetworkTables } from '../utils/networktables'
import ControlButton from '../components/ControlButton'
import Indicator from '../components/Indicator'
import BallCounter from '../components/BallCounter'
// import MotorChart from '../components/MotorChart'

import * as logger from '../utils/logger'


export default {
    name: 'DriverPage',

    components: {
        ControlButton,
        Indicator,
        BallCounter,
        // MotorChart
    },

    data: function() {
        return {
            autoModeValue: 0,
            colorSelectionValue: 0,
            autoModes: [
                {
                    text: 'Basic Mode',
                    value: 0
                },{
                    text: 'Advanced Mode',
                    value: 1
                },{
                    text: 'Super Mode',
                    value: 2
                }
            ],
            colorModes: [
                {
                    text: 'Red',
                    value: 0
                },{
                    text: 'Yellow',
                    value: 1
                },{
                    text: 'Blue',
                    value: 2
                },{
                    text: 'Green',
                    value: 3
                }
            ]
        }
    },
    methods: {
        updateAutoModes: function(auto){
            NetworkTables.putValue("/SmartDashboard/autoMode", auto);
        },

        getImgSrc: function() {
            return require(`../assets/field-${this.autoModeValue}.jpg`);
        },

        endMatch: function() {
            console.log("CLIMB  - ",  true);
            logger.endMatchProcessing();
        }
    },
};
</script>

<template>
<v-container class="fill-height" fluid>
    <!-- <MotorChart class="chart" /> -->

    <v-row no-gutters>
        <v-col cols="6">
            <video class="video">
                <source src="http://limelight.10.29.90.2:5801" type="video/mp4">
            </video>
        </v-col>
        <v-col cols="2">
            <div class="indicators">
                <indicator icon="car-connected" label="Robot Connected" networkKey="diskBrakeStatus"/>
                <indicator icon="robot" label="Robot Enabled" networkKey="diskBrakeStatus"/>
                <indicator icon="robot-mower" label="Intake on" networkKey="intakeOn"/>
                <indicator icon="robot-industrial" label="Intake Down" networkKey="intakeExtended"/>
                <indicator icon="elevator-up" label="Climbing" networkKey="diskBrakeStatus"/>
                <indicator icon="transfer-up" label="Climber Extending" networkKey="diskBrakeStatus"/>
                <indicator icon="ship-wheel" label="Spinning Wheel" spin networkKey="diskBrakeStatus"/>
                <indicator icon="latitude" label="Shooter Spinning" spin networkKey="diskBrakeStatus"/>
                <indicator icon="share-all" label="Shooter Ready" networkKey="diskBrakeStatus"/>
                <BallCounter /> 

            </div>
        </v-col>
        <v-col cols="4">
            <div class="actions">
                <ControlButton togglable label="Toggle Shooter Wheel" networkKey="toggle shooter" />
                <ControlButton label="Shoot Balls" networkKey="toggle shooter" />
                <ControlButton togglable label="Climb" networkKey="climb" />
                <ControlButton label="Reach up" networkKey="reach up" />
                <ControlButton togglable label="Raise/lower Intake" networkKey="toggle intake" />
                <ControlButton label="Spin Color Wheel" networkKey="spin color wheel" />
                <ControlButton label="Set Color" networkKey="set color" />
                <!--
                <v-btn
                    class="auto-button"
                    outlined
                    color="primary"
                    height="55"
                    @click="endMatch()">
                        End Match
                </v-btn>
                -->
            </div>
        </v-col>
    </v-row>
    <v-row no-gutters>
        <v-col cols="12">
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
    </v-row>
</v-container>
</template>

<style scoped lang="scss">
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
        width: 600px;
        height: 450px;
        background: yellow;
    }
</style>
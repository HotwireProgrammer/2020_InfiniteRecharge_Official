<script>
import { NetworkTables } from '../utils/networktables'
import ControlButton from '../components/ControlButton'
import Indicator from '../components/Indicator'

import * as logger from '../utils/logger'

export default {
    name: 'DriverPage',

    components: {ControlButton, Indicator},

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
        endMatch: function() {
            console.log("CLIMB  - ",  true);
            logger.endMatchProcessing();
        }
    },
};
</script>

<template>
<v-container class="fill-height" fluid>

    <div class="video"></div>

    <div class="indicators">
        <indicator icon="car-connected" label="Robot Connected" networkKey="diskBrakeStatus"/>
        <indicator icon="robot" label="Robot Enabled" networkKey="diskBrakeStatus"/>
        <indicator icon="robot-mower" label="Intake on" networkKey="intakeOn"/>
        <indicator icon="robot-industrial" label="Intake Extended" networkKey="intakeExtended"/>
        <indicator icon="share-all" label="Shooter" networkKey="diskBrakeStatus"/>
        <indicator icon="plus-circle-multiple-outline" label="Shooter loaded" networkKey="diskBrakeStatus"/>
        <indicator icon="elevator-up" label="Climbing" networkKey="diskBrakeStatus"/>
        <indicator icon="transfer-up" label="Climber Extending" networkKey="diskBrakeStatus"/>
        <indicator icon="ship-wheel" label="Spinning Wheel" networkKey="diskBrakeStatus"/>
    </div>
    <div class="choices">
        <v-select
            :items="autoModes"
            label="Auto Mode"
            v-model="autoModeValue"
            outlined
            color="primary"
            item-color="primary"
            class="auto-dropdown"
        ></v-select>
        <ControlButton class="auto-button" label="Start Auto" networkKey="start auto" />
    </div>
    <div class="actions">
        <ControlButton class="auto-button" label="Toggle Shooter" networkKey="toggle shooter" />
        <ControlButton class="auto-button" label="Spin Color Wheel" networkKey="spin color wheel" />
        <ControlButton class="auto-button" label="Climb" networkKey="climb" />
        <ControlButton class="auto-button" label="Reach Up" networkKey="reach up" />
        <ControlButton class="auto-button" label="Toggle Intake" networkKey="toggle intake" />
        <ControlButton class="auto-button" label="Set Color" networkKey="set color" />
    </div>
</v-container>
</template>

<style scoped lang="scss">

    .auto-button {
        margin-left: 20px;
        margin-bottom: 3px;
    }

    .auto-dropdown {
        display: inline-block;
        width: 285px;
        color: pink;
    }

    .color-container {
        margin-left: 30px;
    }

    .video {
        width: 400px;
        height: 300px;
        background: black;
    }
</style>
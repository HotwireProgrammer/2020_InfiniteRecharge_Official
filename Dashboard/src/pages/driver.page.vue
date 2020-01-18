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
            ],
            rpmValue: {
                value: 50,
                label: 'RPMs',
                color: 'primary'
            }
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

    <div class="auto-container">
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
    <div class="color-container">
        <v-select
            :items="colorModes"
            label="Select Color"
            v-model="colorSelectionValue"
            outlined
            color="primary"
            item-color="primary"
            class="auto-dropdown"
        ></v-select>
        <ControlButton class="auto-button" label="Set Color" networkKey="set color" />
    </div>
         <v-slider
             v-model="rpmValue.value"
            :label="rpmValue.label"
            :thumb-color="rpmValue.color"
            thumb-label="always"
    ></v-slider>
        <ControlButton class="auto-button" label="Toggle Shooter" networkKey="toggle shooter" />    
        <ControlButton class="auto-button" label="Spin Color Wheel" networkKey="spin color wheel" />
        <ControlButton class="auto-button" label="Climb" networkKey="climb" />
        <ControlButton class="auto-button" label="Reach Up" networkKey="reach up" />
        <ControlButton class="auto-button" label="Toggle Intake" networkKey="toggle intake" />
        <indicator icon="robot-vacuum" label="intake status" networkKey="intake"/>
        <indicator icon="home" label="shooter status" networkKey="shooter"/>
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
</style>
<script>
import { NetworkTables } from '../utils/networktables'
import * as logger from '../utils/logger'

export default {
    name: 'DriverPage',

    components: {},

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
        startAuto: function() {
            NetworkTables.putValue('startAuto', this.autoModeValue);
            console.log("THE AUTO HAS STARTED - ",  this.autoModeValue);
        },
        setColor: function() {
            NetworkTables.putValue('setcolor', this.colorSelectionValue);
            console.log("COLOR - ",  this.colorSelectionValue);
        },
        spinColorWheel: function() {
            NetworkTables.putValue('spinColorWheel', true);
            console.log("COLOR WHEEL - ",  true);
        },
        climb: function() {
            NetworkTables.putValue('climb', true);
            console.log("CLIMB  - ",  true);
            logger.endMatchProcessing();
        },
        endMatch: function() {
            console.log("CLIMB  - ",  true);
            logger.endMatchProcessing();
        },
        reachUp: function() {
            NetworkTables.putValue('reachUp', true);
            console.log("REACH UP  - ",  true);
        },
        toggleIntake: function() {
            NetworkTables.putValue('toggleIntake', true);
            console.log("TOGGLE INTAKE  - ",  true);
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
        <v-btn
            class="auto-button"
            outlined
            color="primary"
            height="55"
            @click="startAuto()">Start Auto</v-btn>
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
        <v-btn
            class="auto-button"
            outlined
            color="primary"
            height="55"
            @click="setColor()">Set Color</v-btn>
    </div>
        <v-btn
            class="auto-button"
            outlined
            color="primary"
            height="55"
            @click="spinColorWheel()">Spin Color Wheel</v-btn>
    <v-btn
            class="auto-button"
            outlined
            color="primary"
            height="55"
            @click="climb()">Climb</v-btn>
        <v-btn
            class="auto-button"
            outlined
            color="primary"
            height="55"
            @click="reachUp()">Reach Up</v-btn>
    <v-btn
            class="auto-button"
            outlined
            color="primary"
            height="55"
            @click="toggleIntake()">Toggle Intake</v-btn>
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
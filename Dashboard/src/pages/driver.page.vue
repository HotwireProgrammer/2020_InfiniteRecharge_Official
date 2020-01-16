<script>
import PDIChart from '../components/PDIChart';
import { NetworkTables } from '../utils/networktables'


// const autoModeList = [{
//     text: 'Basic Mode',
//     value: 0
// },
// {
//     text: 'Advanced Mode',
//     value: 0
// },
// {
//     text: 'Super Mode',
//     value: 0
// }];

export default {
    name: 'DriverPage',

    components: {
        PDIChart
    },

    data: function() {
        return {
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

    data: () => ({ data: null}),
};
</script>

<template>
    <v-container class="fill-height" fluid>
        <PDIChart class="pdi-chart" />
        <div class="auto-container">
            <v-select
                :items="[{text: 'Basic Mode', value: 0},{text: 'Advanced Mode', value: 1},{text: 'Super Mode', value: 2}]"
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
                :items="[{text: 'Red', value: 0},{text: 'Yellow', value: 1},{text: 'Blue', value: 2},{text: 'Green', value: 3}]"
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

    .pdi-chart {
        width: 600px;
    }

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
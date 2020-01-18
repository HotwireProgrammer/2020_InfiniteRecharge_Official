<script>
import { VueApexCharts } from 'vue-apexcharts'
import { NetworkTables } from '../utils/networktables'
import * as logger from '../utils/logger'
// import Vue from 'vue';


function addData() {

}


export default {
    name: 'UltrasonicDownCounter',
    components: {},
    data: () => {
        return {
            value: 0
        }
    },
    methods: {
        updateUltrasonicDown: function (newValue) {
            this.value = newValue;
            logger.logData('UltrasonicDown', newValue)
        }
    },
    mounted: function() {
        NetworkTables.addKeyListener('/SmartDashboard/UltrasonicDown', this.updateUltrasonicDown);
        setInterval(() => {
            this.updateUltrasonicDown(this.value + 1);
        }, 1000);
    }
};

</script>

<template>
    <div>
        {{value}}
    </div>
</template>
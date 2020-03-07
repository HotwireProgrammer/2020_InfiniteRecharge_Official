<script>
import PIDChart from '../components/PIDChart';
import PidInputs from '../components/PidInputs';
import MotorChart from '../components/MotorChart'
import TempChart from '../components/TempChart'
import * as logger from '../utils/logger'

export default {
    name: 'DetailedPage',

    components: {
        PIDChart,
        MotorChart,
        TempChart,
        PidInputs
    },

    data: function() {
        return {
            pidList: ["thing1", "thing2"]
        };
    },  
    methods: {
        endMatch: function() {
            console.log("End Match  - ",  true);
            logger.endMatchProcessing();
        }
    }
};
</script>

<template>
<v-container class="fill-height" fluid>
    <v-row>
        <v-col cols="6">
            <MotorChart class="amp-chart" />
        </v-col>
        <v-col cols="6">
            <TempChart class="amp-chart" />
        </v-col>
    </v-row>
    <v-row>
        <v-col cols="12">
            <PIDChart class="pid-chart" />
            <div class="actions" v-for="str in pidList" :key="str">
                <PidInputs :baseStr="str" />
            </div>
        </v-col>
    </v-row>

</v-container>
</template>

<style scoped lang="scss">>
.pid-chart {
    width: 100%;
}
.amp-chart {
    width: 100%;
}
</style>

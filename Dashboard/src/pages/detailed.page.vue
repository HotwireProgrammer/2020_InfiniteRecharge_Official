<script>
import PIDChart from '../components/PIDChart';
import PidInputs from '../components/PidInputs';
import MotorChart from '../components/MotorChart'
import * as logger from '../utils/logger'

export default {
    name: 'DetailedPage',

    components: {
        PIDChart,
        MotorChart,
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
    <PIDChart class="pid-chart" />
    <MotorChart class="chart" />
    <v-col cols="6">
        <div class="actions" v-for="str in pidList" :key="str">
            <PidInputs :baseStr="str" />
        </div>
    </v-col>

</v-container>
</template>

<style scoped lang="scss">>
.pid-chart {
    width: 600px;
}
</style>

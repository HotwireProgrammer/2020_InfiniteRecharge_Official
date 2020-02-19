<script>
import PIDChart from '../components/PIDChart';
import MotorChart from '../components/MotorChart'
import ControlButton from '../components/ControlButton'
import * as logger from '../utils/logger'

export default {
    name: 'DetailedPage',

    components: {
        PIDChart,
        MotorChart,
        ControlButton
    },

    data: () => ({ data: null}),
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

    <v-col cols="3">
        <div class="actions">
            <ControlButton togglable label="Toggle Shooter Wheel" networkKey="toggle shooter" />
            <ControlButton label="Shoot Balls" networkKey="toggle shooter" />
            <ControlButton togglable label="Climb" networkKey="climb" />
            <ControlButton label="Reach up" networkKey="reach up" />
            <ControlButton togglable label="Raise/lower Intake" networkKey="toggle intake" />
            <ControlButton label="Spin Color Wheel" networkKey="spin color wheel" />
            <ControlButton label="Set Color" networkKey="set color" />
            <v-btn
                class="auto-button"
                outlined
                color="primary"
                height="55"
                @click="endMatch()">
                    End Match
            </v-btn>
        </div>
    </v-col>

</v-container>
</template>

<style scoped lang="scss">>
.pid-chart {
    width: 600px;
}
</style>

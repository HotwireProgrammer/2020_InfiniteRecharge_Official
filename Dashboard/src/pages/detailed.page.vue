<script>
// import PowerChart from '../components/PowerChart';
// import PressureButtons from '../components/PressureButtons';
// import UltrasonicDownCounter from '../components/UltrasonicDownCounter';
import PIDChart from '../components/PIDChart';

export default {
    name: 'DetailedPage',

    components: {
        // PowerChart,
        // PressureButtons,
        // UltrasonicDownCounter,
        PIDChart
    },

    data: () => ({ data: null}),
};
</script>

<template>
<v-container class="fill-height" fluid>
    <!-- <PowerChart />
    <PressureButtons />
    <UltrasonicDownCounter /> -->
    <PIDChart class="pid-chart" />
</v-container>
</template>

<style scoped lang="scss">>
.pid-chart {
    width: 600px;
}
</style>

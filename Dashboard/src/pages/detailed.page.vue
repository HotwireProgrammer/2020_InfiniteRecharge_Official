<script>
// import PowerChart from '../components/PowerChart';
// import PressureButtons from '../components/PressureButtons';
// import UltrasonicDownCounter from '../components/UltrasonicDownCounter';
import PDIChart from '../components/PDIChart';

export default {
    name: 'DetailedPage',

    components: {
        // PowerChart,
        // PressureButtons,
        // UltrasonicDownCounter,
        PDIChart
    },

    data: () => ({ data: null}),
};
</script>

<template>
<v-container class="fill-height" fluid>
    <!-- <PowerChart />
    <PressureButtons />
    <UltrasonicDownCounter /> -->
    <PDIChart class="pdi-chart" />
</v-container>
</template>

<style scoped lang="scss">>
.pdi-chart {
    width: 600px;
}
</style>

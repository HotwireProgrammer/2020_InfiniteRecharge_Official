<script>
import PIDChart from '../components/PIDChart';

export default {
    name: 'DetailedPage',

    components: {
        PIDChart
    },

    data: () => ({ data: null}),
};
</script>

<template>
<v-container class="fill-height" fluid>
    <PIDChart class="pid-chart" />
</v-container>
</template>

<style scoped lang="scss">>
.pid-chart {
    width: 600px;
}
</style>

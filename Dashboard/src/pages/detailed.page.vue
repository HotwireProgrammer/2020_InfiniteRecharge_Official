<script>
import PowerChart from '../components/PowerChart';
import PressureButtons from '../components/PressureButtons';
import UltrasonicDownCounter from '../components/UltrasonicDownCounter';

export default {
  name: 'DetailedPage',

  components: {
    PowerChart, PressureButtons, UltrasonicDownCounter
  },

  data: () => ({ data: null}),
};
</script>

<template>
      <v-container class="fill-height" fluid>
        <PowerChart />
        <PressureButtons />
        <UltrasonicDownCounter />
      </v-container>
</template>

<script>
import { NetworkTables } from '../utils/networktables'

export default {
    name: 'MatchTimer',

    components: {},
    mounted: function(){
        if (this.networkKey) {
            NetworkTables.addKeyListener('/SmartDashboard/MatchTime', this.updateTime);
        }
        // Test
        // setInterval(() => {
        //     console.log('inv');
        //     this.updateTime('', this.time-1)
        // }, 1000);
    },
    data: function () { 
        return { 
            time: 0,
            timeStr: '0:00',
            timeColor: '#00ff4e'
        }
    },
    methods: {
        updateTime: function(key, newValue) {
            this.time = newValue;
            let secs = newValue % 60;
            let mins = Math.floor(newValue / 60);
            this.timeStr = mins + ':' + ("0" + secs).slice(-2)
            if (newValue < 15) {
                this.timeColor = '#f44336';
            } else if (newValue < 30) {
                this.timeColor = '#ffc107';
            } else {
                this.timeColor = '#00ff4e';
            }
        }
    }
};
</script>

<template>
<div class="time" :style="{ color: timeColor }">
    {{timeStr}}
</div>
</template>

<style scoped lang="scss">
.time {
    font-size: 83px;
    line-height: 1;
    padding-bottom: 20px;
}

</style>

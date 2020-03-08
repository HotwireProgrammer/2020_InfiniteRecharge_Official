<script>
import { NetworkTables } from '../utils/networktables'

export default {
    name: 'Indicator',

    components: {},
    mounted:function(){
        if (this.networkKey) {
            NetworkTables.addKeyListener('/SmartDashboard/' + this.networkKey, this.updateIndicator);
        }
    },
    data: function () { 
        return { 
            toggledOn: this.status,
            // activeColor: this.warn ? 'pink' : 'red'
            activeColor: this.warn ? '#f44336' : '#00ff4e'
        }
    },
    props: {
        label: {
            type: String,
            required: true
        },
        icon: {
            type: String,
            required: true
        },
        networkKey: String,
        status: {
            type: Boolean
        },
        warn: Boolean,
        spin: Boolean
    },
    methods: {
        updateIndicator: function(key, newValue) {
            this.toggledOn = newValue;
        },
    }
};
</script>

<template>
<div>
    <v-icon 
        class="icon"
        v-bind:class="{'mdi-spin': status && spin}"
        v-bind:color="(status ? status : toggledOn) ? activeColor : 'white'"
        x-large>
            mdi-{{icon}}
    </v-icon>
    <span v-bind:class="{ active: status ? status : toggledOn, warn: warn }">{{label}}</span>
</div>
</template>

<style scoped lang="scss">

.icon {
    margin-right: 10px;
}

.active {
    color: #00ff4e;

    &.warn {
        color: #f44336
    }
}

</style>

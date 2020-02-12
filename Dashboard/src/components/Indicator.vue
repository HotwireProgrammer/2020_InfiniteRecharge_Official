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
    data: () => (
        {}
    ),
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
        status: Boolean,
        spin: Boolean
    },
    methods: {
        updateIndicator: function(key, newValue) {
            this.status = newValue;
        },
    }
};
</script>

<template>
<div>
    <v-icon 
        class="icon"
        v-bind:class="{'mdi-spin': this.status && this.spin}"
        v-bind:color="status ? '#00ff4e' : 'white'"
        x-large>
            mdi-{{icon}}
    </v-icon>
    <span v-bind:style="'color:' + status ? '#00ff4e' : 'white'">{{label}}</span>
</div>
</template>

<style scoped lang="scss">

.icon {
    margin-right: 10px;
}

</style>

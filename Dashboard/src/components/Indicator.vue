<script>
import { NetworkTables } from '../utils/networktables'

export default {
    name: 'Indicator',

    components: {},
    mounted:function(){
        NetworkTables.addKeyListener('/SmartDashboard/' + this.networkKey, this.updateIndicator);

},
    data: () => (
        {
            status: false,
            color: 'white'
        }
    ),
    props: {
        label: {
            type: String,
            required: true
        },
         networkKey: {
            type: String,
            required: true
        },
        icon: {
            type: String,
            required: true
        }
    },
    methods: {
        updateIndicator: function(key, newValue) {
            this.status = newValue;
            if(newValue){
                this.color='#00ff4e'
            }else{
                this.color='white'
            }
        },
    }
};
</script>

<template>
<div>
    <v-icon class="icon" v-bind:color="color" large>mdi-{{icon}}</v-icon>
    <span v-bind:style="'color:'+color">{{label}}</span>
</div>
</template>

<style scoped lang="scss">

.icon {
    margin-right: 10px;
}

</style>

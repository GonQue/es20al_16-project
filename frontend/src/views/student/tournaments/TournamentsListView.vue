<template>

</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { Tournament } from '@/models/user/Tournament';
import CreateTournamentDialog from '@/views/student/tournaments/CreateTournamentDialog.vue';

@Component({
  components: {
    'create-tournament-dialog': CreateTournamentDialog,
  }
})
export default class TournamentsListView extends Vue {

}
</script>

<style lang="scss" scoped></style>

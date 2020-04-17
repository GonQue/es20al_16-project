<template>
  <v-card class="table">
    <v-data-table
            :headers="headers"
            :search="search"
            disable-pagination
            :hide-default-footer="true"
            :mobile-breakpoint="0"
            multi-sort
    >
    <template v-slot:top>
      <v-card-title>
        <v-text-field
          v-model="search"
          append-icon="search"
          label="Search"
          class="mx-2"
        />
        <v-spacer />
        <v-btn color="primary" dark @click="createTournament" data-cy="createButton">New Tournament</v-btn>

      </v-card-title>
    </template>


    </v-data-table>
    <create-tournament-dialog
      v-if="tournament"
      v-model="createTournamentDialog"
      :tournament="tournament"
      v-on:new-tournament="onCreateTournament"
      v-on:close-dialog="onCloseDialog"
    />
  </v-card>
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
  createTournamentDialog: boolean = true;
  tournament : Tournament | null = null;
  search: string = '';
  headers: object = [
    {
      text: 'Course Type',
      value: 'courseType',
      align: 'center',
      width: '10%'
    },
    { text: 'Name', value: 'name', align: 'left', width: '30%' },
    {
      text: 'Execution Type',
      value: 'courseExecutionType',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Acronym',
      value: 'acronym',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Academic Term',
      value: 'academicTerm',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Status',
      value: 'status',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      sortable: false,
      width: '20%'
    }
  ];


  createTournament() {
    this.tournament = new Tournament();
    this.createTournamentDialog = true;
  }

  onCloseDialog() {
    this.createTournamentDialog = false;
  }

  onCreateTournament(){
    this.createTournamentDialog = false;
  }


}
</script>

<style lang="scss" scoped></style>

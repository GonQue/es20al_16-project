<template>
  <div class="container">
    <v-card>
      <v-card-title class="justify-center">
        Hold on and wait {{ timer() }} to start the Tournament!
      </v-card-title>
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import StatementQuiz from '../../../models/statement/StatementQuiz';
import { milisecondsToHHMMSS } from '@/services/ConvertDateService';
import RemoteServices from '@/services/RemoteServices';
import StatementManager from '@/models/statement/StatementManager';
@Component
export default class TournamentStartView extends Vue {
  statementQuiz: StatementQuiz | null = null;
  tournamentId: number | undefined | null = null;

  async created() {
    this.statementQuiz = StatementManager.getInstance.statementQuiz;
    this.tournamentId = this.statementQuiz?.tournamentId;
  }

  async getTournamentQuiz() {
    if (
      this.statementQuiz &&
      this.tournamentId &&
      this.$router.currentRoute.name === 'tournament-start'
    ) {
      try {
        this.statementQuiz = await RemoteServices.getTournamentQuiz(
          this.tournamentId
        );

        if (this.statementQuiz.timeToAvailability === 0) {
          let statementManager: StatementManager = StatementManager.getInstance;
          statementManager.statementQuiz = this.statementQuiz;
          await this.$router.push({ name: 'solve-quiz' });
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
        await this.$router.push({ name: 'home' });
      }
    }
  }

  timer() {
    if (this.statementQuiz?.timeToAvailability === 0) {
      this.getTournamentQuiz();
    }

    return milisecondsToHHMMSS(this.statementQuiz?.timeToAvailability);
  }
}
</script>

<style lang="scss" scoped></style>

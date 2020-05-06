<template>
    <div class="container">
        <v-card>
            <v-card-title class="justify-center">
                Hold on and wait {{ timer() }} to start the quiz!
            </v-card-title>
        </v-card>
    </div>
</template>

<script>
  import StatementQuiz from '../../../models/statement/StatementQuiz';

  @Component
  export default class TournamentStartView extends Vue {
    quizId: number | null = null;
    quiz: StatementQuiz | null = null;

    async created() {
      if (!this.statementQuiz?.id) {
        await this.$router.push({ name: 'create-quiz' });
      } else {
        try {
          await RemoteServices.startQuiz(this.statementQuiz?.id);
        } catch (error) {
          await this.$store.dispatch('error', error);
          await this.$router.push({ name: 'available-quizzes' });
        }
      }

  }

</script>



<style scoped>

</style>
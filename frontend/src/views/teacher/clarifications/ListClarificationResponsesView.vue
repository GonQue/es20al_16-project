<template>
    <v-card class="table">
        <v-data-table
                :headers="headers"
                :items="clarificationResponses"
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
                </v-card-title>
            </template>

            <template v-slot:item.deleteClarificationResponse="{ item }">
                <v-tooltip bottom>
                    <template v-slot:activator="{ on }">
                        <v-icon
                                class="mr-2"
                                v-on="on"
                                @click="deleteClarificationResponse(item.id)"
                                color="#D32F2F"
                                data-cy="DeleteClarificationResponseIcon"
                        >delete</v-icon>
                    </template>
                    <span>Remove Response</span>
                </v-tooltip>
            </template>
        </v-data-table>
    </v-card>
</template>

<script lang="ts">
  import { Component, Prop, Vue } from 'vue-property-decorator';
  import StatementClarificationResponse from '@/models/statement/StatementClarificationResponse';
  import RemoteServices from '@/services/RemoteServices';

  @Component
  export default class ListClarificationResponses extends Vue {
    @Prop(String) readonly clarificationQuestionId!: string;
    clarificationResponses: StatementClarificationResponse[] = [];
    search: string = '';
    headers: object = [
      {
        text: 'Response Content',
        value: 'teacherResponse',
        align: 'center',
        width: '35%'
      },
      {
        text: 'Response Date',
        value: 'responseDate',
        align: 'center',
        width: '15%'
      },
      {
        text: 'Remove',
        value: 'deleteClarificationResponse',
        align: 'center',
        width: '5%'
      }
    ];

    async created() {
      await this.$store.dispatch('loading');
      try {
        if (this.$router.currentRoute.fullPath.includes("management")) {
          this.clarificationResponses = await RemoteServices.getTeacherClarificationResponse(parseInt(this.clarificationQuestionId));
        } else
          this.clarificationResponses = await RemoteServices.getStudentClarificationResponse(parseInt(this.clarificationQuestionId));
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }

    async deleteClarificationResponse(clarificationResponseId: number) {
      if (confirm('Are you sure you want to delete this clarification response?')) {
        try {
          await RemoteServices.deleteClarificationResponse(clarificationResponseId);
          this.clarificationResponses = this.clarificationResponses.filter(
            clarificationResponse => clarificationResponse.id != clarificationResponseId
          );
        } catch (error) {
          await this.$store.dispatch('error', error);
        }
        await this.$store.dispatch('clearLoading');
      }
    }
  }

</script>

<style lang="scss" scoped />

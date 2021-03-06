<template>
  <div class="q-pa-md">
    <q-table
      :rows="rows"
      :columns="columns"
      row-key="name"
      no-data-label="I didn't find anything for you. Consider creating a new Lav Mapping."
      no-results-label="The filter didn't uncover any results"
    >
      <template v-slot:top-left="">
        <div class="q-table__title">
          {{ title }}
          <q-btn
            padding="none"
            color="secondary"
            icon="add"
            @click="show_dialog = true"
          />
        </div>

        <q-dialog v-model="show_dialog" persistent>
          <q-card style="width: 700px; max-width: 80vw">
            <q-card-section>
              <div class="text-h6">New LAVMapping(owl:sameAs)</div>
            </q-card-section>

            <q-card-section>
              <q-form @submit="onSubmit" @reset="onReset" class="q-gutter-md">
                <div class="row">
                  <div class="col">
                    <q-select
                      v-model="selectedWrapper"
                      :options="wrapper ? wrapper.map((e) => e.name) : []"
                      label="Wrapper"
                    />
                    <h6>Attributes</h6>
                    <q-field
                      outlined
                      v-for="at in getAttributes()"
                      :key="at"
                      stack-label
                    >
                      <template v-slot:control>
                        <div
                          class="self-center full-width no-outline"
                          tabindex="0"
                        >
                          {{ at }}
                        </div>
                      </template>
                    </q-field>
                  </div>
                  <div class="col">
                    <q-select
                      v-model="selectedGlobalGraph"
                      :options="
                        globalGraphsContent
                          ? globalGraphsContent.map((e) => e.name)
                          : []
                      "
                      label="Global Graph"
                    />
                    <h6>Features</h6>
                    <q-select
                      v-model="selectedFeatures[elem]"
                      v-for="elem in Array.from(
                        Array(getAttributes().length).keys()
                      )"
                      :key="elem"
                      :options="getFeaturesFromGlobalGraph()"
                    />
                  </div>
                </div>
                <div>
                  <q-btn label="Submit" type="submit" color="primary" />
                  <q-btn
                    label="Cancel"
                    type="reset"
                    color="primary"
                    flat
                    class="q-ml-sm"
                  />
                </div>
              </q-form>
            </q-card-section>
          </q-card>
        </q-dialog>
        <q-dialog v-model="show_edit_dialog" persistent>
          <q-card style="width: 700px; max-width: 80vw">
            <q-card-section>
              <div class="text-h6">Edit LAVMapping</div>
            </q-card-section>

            <q-card-section>
              <q-form
                @submit="onSubmitEdit"
                @reset="onReset"
                class="q-gutter-md"
              >
                <div class="row">
                  <div class="col">
                    <h6>{{selectedWrapper}}</h6>
                    <q-field
                      outlined
                      v-for="at in getAttributes()"
                      :key="at"
                      stack-label
                    >
                      <template v-slot:control>
                        <div
                          class="self-center full-width no-outline"
                          tabindex="0"
                        >
                          {{ at }}
                        </div>
                      </template>
                    </q-field>
                  </div>
                  <div class="col">
                    <h6>{{selectedGlobalGraph}}</h6>
                    <q-select
                      v-model="selectedFeatures[elem]"
                      v-for="elem in Array.from(
                        Array(getAttributes().length).keys()
                      )"
                      :key="elem"
                      :options="getFeaturesFromGlobalGraph()"
                    />
                  </div>
                </div>
                <div>
                  <q-btn label="Submit" type="submit" color="primary" />
                  <q-btn
                    label="Cancel"
                    type="reset"
                    color="primary"
                    flat
                    class="q-ml-sm"
                  />
                </div>
              </q-form>
            </q-card-section>
          </q-card>
        </q-dialog>
      </template>

      <template v-slot:top-right="props">
        <q-btn
          flat
          round
          dense
          :icon="props.inFullscreen ? 'fullscreen_exit' : 'fullscreen'"
          @click="props.toggleFullscreen"
        >
          <q-tooltip :disable="$q.platform.is.mobile" v-close-popup>
            {{ props.inFullscreen ? "Exit Fullscreen" : "Toggle Fullscreen" }}
          </q-tooltip>
        </q-btn>
      </template>
      <template v-slot:body-cell-actions="props">
        <q-td :props="props">
          <q-btn
            dense
            round
            flat
            color="grey"
            @click="deleteRow(props)"
            icon="delete"
          ></q-btn>
        </q-td>
      </template>
      <template v-slot:body-cell-sameAs="props">
        <q-td :props="props">
          <q-btn
            dense
            round
            flat
            color="grey"
            @click="editRow(props)"
            icon="search"
          ></q-btn>
        </q-td>
      </template>
      <template v-slot:body-cell-covered="props">
        <q-td :props="props">
          <q-btn
            dense
            round
            flat
            color="grey"
            :to="
              '/LAVMappings/subgraphselect/' +
              props.row.globalGraphId +
              '/' +
              props.row.id
            "
            icon="search"
          ></q-btn>
        </q-td>
      </template>
      <template v-slot:body-cell-status="props">
        <q-td :props="props">
          <q-chip
            text-color="white"
            color="accent"
            v-if="props.row.globalQuery === null"
          >
            missing global query</q-chip
          >
          <q-chip text-color="white" color="blue" v-else> Completed</q-chip>
        </q-td>
      </template>

      <template v-slot:no-data="{ icon, message, filter }">
        <div class="full-width row flex-center text-accent q-gutter-sm">
          <q-icon size="2em" name="sentiment_dissatisfied" />
          <span> Well this is sad... {{ message }} </span>
          <q-icon size="2em" :name="filter ? 'filter_b_and_w' : icon" />
        </div>
      </template>
    </q-table>
  </div>
</template>



<script >
import { defineComponent, ref } from "vue";
import { odinApi } from "boot/axios";

export default defineComponent({
  name: "TableDataSources",
  // props:{
  //   rows: {
  //     type: Array,
  //     default() {
  //       return []
  //     }
  //   }
  // },
  data() {
    const columns = [
      {
        name: "Wrapper",
        label: "Wrapper",
        align: "center",
        field: "wrapper",
        sortable: true,
      },
      {
        name: "Global Graph",
        label: "Global Graph",
        align: "center",
        field: "globalGraph",
        sortable: false,
      },
      {
        name: "sameAs",
        label: "owl:sameAs",
        align: "center",
        field: "sameAs",
        sortable: false,
      },
      {
        name: "covered",
        label: "Covered Subgraph",
        align: "center",
        field: "Covered Subgraph",
        sortable: false,
      },
      {
        name: "status",
        label: "status",
        align: "center",
        field: "status",
        sortable: true,
      },
      {
        name: "actions",
        label: "delete",
        align: "center",
        field: "actions",
        sortable: false,
      },
    ];
    const rows = [];
    const globalGraphsContent = [];
    const wrapper = [];
    const globalGraphsLabels= [];
    const title = "LAVMappings";
    const show_dialog = false;
    const selectedGlobalGraph = "";
    const selectedWrapper = "";
    const selectedLavMappingId = "";
    const newLavMapping = {
      id: "",
      wrapperId: "",
      globalGraphId: "",
      sameAs: [
        {
          feature: "",
          attribute: "",
        },
      ],
      graphicalSubgraph: "",
    };
    const features = {
      namedGraph: "",
      featuresArr: [],
    };
    const selectedFeatures = [];
    const show_edit_dialog = false;

    return {
      columns,
      rows,
      title,
      show_dialog,
      globalGraphsContent,
      globalGraphsLabels,
      wrapper,
      selectedWrapper,
      selectedGlobalGraph,
      newLavMapping,
      features,
      selectedFeatures,
      show_edit_dialog,
      selectedLavMappingId,
    };
  },
  mounted() {
    this.newLavMapping.sameAs = [];
    this.retrieveData();
  },
  methods: {
    onSubmit() {
      const attibs = this.getAttributes();
      var i = 0;
      for (const at of attibs) {
        const obj = {
          attribute: at,
          feature: this.selectedFeatures[i],
        };
        this.newLavMapping.sameAs.push(obj);
        i++;
      }

      this.newLavMapping.wrapperId = this.wrapper.filter(
        (e) => e.name === this.selectedWrapper
      )[0].id;
      this.newLavMapping.globalGraphId = this.globalGraphsContent.filter(
        (e) => e.name === this.selectedGlobalGraph
      )[0].id;
      odinApi.post("/lavMapping", this.newLavMapping).then((response) => {
        if (response.status == 201) {
          response.data.wrapper = this.wrapper.filter(
            (elem) => elem.id === response.data.wrapperId
          )[0].name;
          response.data.globalGraph = this.globalGraphsContent.filter(
            (elem) => elem.id === response.data.globalGraphId
          )[0].name;
          this.rows.push(response.data);
          this.show_dialog = false;
        }
      });
    },

    onReset() {
      this.show_dialog = false;
      this.show_edit_dialog = false;
      this.selectedWrapper = "";
      this.selectedGlobalGraph = "";
      this.newLavMapping = {
        id: "",
        wrapperId: "",
        globalGraphId: "",
        sameAs: [],
        graphicalSubgraph: "",
      };
    },

    retrieveData() {
      odinApi
        .get("/globalGraph")
        .then((response) => {
          if (response.status == 200) {
            this.globalGraphsContent = response.data;
            for (const elem of response.data) {
              this.globalGraphsLabels.push(elem.name);
            }
          }
        })
        .then(() =>
          odinApi.get("/wrapper").then((response) => {
            if (response.status == 200) {
              this.wrapper = response.data;
            }
          })
        )
        .then(() =>
          odinApi.get("/lavMapping").then((response) => {
            if (response.status == 200 || response.status == 204) {
              if (response.data) {
                response.data = response.data.map(
                  (e) =>
                    (e = {
                      wrapper:
                        this.wrapper.filter((elem) => elem.id === e.wrapperId)
                          .length > 0
                          ? this.wrapper.filter(
                              (elem) => elem.id === e.wrapperId
                            )[0].name
                          : undefined,
                      globalGraph:
                        this.globalGraphsContent.filter(
                          (elem) => elem.id === e.globalGraphId
                        ).length > 0
                          ? this.globalGraphsContent.filter(
                              (elem) => elem.id === e.globalGraphId
                            )[0].name
                          : undefined,
                      id: e.id,
                      wrapperId: e.wrapperId,
                      globalGraphId: e.globalGraphId,
                      sameAs: e.sameAs,
                      globalQuery: e.globalQuery,
                    })
                );
                console.log("ROWS:");
                console.log(response.data);
                this.rows = response.data;
              } else {
                this.rows = [];
              }
            }
          })
        );
    },
    getFeaturesFromGlobalGraph() {
      const globalGraph = this.globalGraphsContent.find(
        (e) => e.name === this.selectedGlobalGraph
      );
      if (globalGraph) {
        if (globalGraph.namedGraph === this.features.namedGraph)
          return this.features.featuresArr;
        odinApi
          .get(`/globalGraph/${globalGraph.id}/featuresConcepts`)
          .then((response) => {
            this.features.namedGraph = globalGraph.namedGraph;
            this.features.featuresArr = response.data;
          });
      } else console.log("Not found");
    },
    getAttributes() {
      return this.selectedWrapper !== ""
        ? this.wrapper
            .filter((e) => e.name === this.selectedWrapper)[0]
            .attributes.map((e) => e.name)
        : [];
    },
    deleteRow(props) {
      odinApi.delete(`/lavMapping/${props.row.id}`).then((response) => {
        if (response.status == 204) {
          this.$q.notify({
            color: "positive",
            textColor: "white",
            icon: "check_circle",
            message: "Successfully deleted",
          });

          this.rows.splice(props.rowIndex, 1);
        } else {
          // 500
          this.$q.notify({
            message: "Something went wrong in the server.",
            color: "negative",
            icon: "cancel",
            textColor: "white",
          });
        }
      });
    },
    editRow(props) {
      console.log("editRow");
      const row = props.row;
      console.log({ row });
      this.selectedWrapper = row.wrapper;
      this.selectedGlobalGraph = row.globalGraph;
      this.selectedLavMappingId = row.id;
      odinApi
        .get(`/globalGraph/${row.globalGraphId}/featuresConcepts`)
        .then((response) => {
          console.log("FEATURES");
          console.log(response.data);
          console.log("ATTRIBUTES");
          console.log(this.getAttributes())
          this.selectedFeatures = [];
          const sameAs = row.sameAs;
          for (var i = 0; i < sameAs.length; ++i) {
            console.log(sameAs[i].feature);
            this.selectedFeatures[i] = sameAs[i].feature;
          }
          this.show_edit_dialog = true;
        });
      //selectedFeatures
      //Edit Struct; edit_data
      /*
      {

      }
      */
    },
    onSubmitEdit(props) {
      this.show_edit_dialog = false;
      console.log(this.selectedLavMappingId);
      console.log(this.selectedFeatures);
      const attibs = this.getAttributes();
      var sameAsArr = [{
          attribute: "",
          feature: "",
        }];
      sameAsArr.pop();

      var i = 0;
      for (const at of attibs) {
        const obj = {
          attribute: at,
          feature: this.selectedFeatures[i],
        };
        sameAsArr.push(obj);
        i++;
      }
      console.log({sameAsArr})
      odinApi
        .put(`/lavMapping/${this.selectedLavMappingId}`, sameAsArr)
        .then((response) => {
          console.log({response});
          if (response.status === 201 || response.status === 204) {
            console.log("Editing");
          }
        });
    },
  },
});
</script>

<style lang="css" scoped>
</style>

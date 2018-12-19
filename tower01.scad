translate ([0, 0, 0]) {
  union () {
    rotate (a=0.0, v=[0, 0, 1]) {
      translate ([0, 0, 0]) {
        union () {
          difference () {
            union () {
              rotate (a=89.95437383553926, v=[0, 0, 1]) {
                translate ([100, 0, 0]) {
                  rotate (a=44.97718691776963, v=[0, 1, 0]) {
                    difference () {
                      cylinder (h=100, r=50, center=true);
                      cylinder (h=104, r=46, center=true);
                    }
                  }
                }
              }
              rotate (a=179.90874767107852, v=[0, 0, 1]) {
                translate ([100, 0, 0]) {
                  rotate (a=44.97718691776963, v=[0, 1, 0]) {
                    difference () {
                      cylinder (h=100, r=50, center=true);
                      cylinder (h=104, r=46, center=true);
                    }
                  }
                }
              }
              rotate (a=269.86312150661774, v=[0, 0, 1]) {
                translate ([100, 0, 0]) {
                  rotate (a=44.97718691776963, v=[0, 1, 0]) {
                    difference () {
                      cylinder (h=100, r=50, center=true);
                      cylinder (h=104, r=46, center=true);
                    }
                  }
                }
              }
              rotate (a=359.81749534215703, v=[0, 0, 1]) {
                translate ([100, 0, 0]) {
                  rotate (a=44.97718691776963, v=[0, 1, 0]) {
                    difference () {
                      cylinder (h=100, r=50, center=true);
                      cylinder (h=104, r=46, center=true);
                    }
                  }
                }
              }
            }
            cylinder (h=164, r=96, center=true);
          }
          difference () {
            difference () {
              cylinder (h=160, r=100, center=true);
              cylinder (h=164, r=96, center=true);
            }
            union () {
              rotate (a=89.95437383553926, v=[0, 0, 1]) {
                translate ([100, 0, 0]) {
                  rotate (a=44.97718691776963, v=[0, 1, 0]) {
                    cylinder (h=100, r=50, center=true);
                  }
                }
              }
              rotate (a=179.90874767107852, v=[0, 0, 1]) {
                translate ([100, 0, 0]) {
                  rotate (a=44.97718691776963, v=[0, 1, 0]) {
                    cylinder (h=100, r=50, center=true);
                  }
                }
              }
              rotate (a=269.86312150661774, v=[0, 0, 1]) {
                translate ([100, 0, 0]) {
                  rotate (a=44.97718691776963, v=[0, 1, 0]) {
                    cylinder (h=100, r=50, center=true);
                  }
                }
              }
              rotate (a=359.81749534215703, v=[0, 0, 1]) {
                translate ([100, 0, 0]) {
                  rotate (a=44.97718691776963, v=[0, 1, 0]) {
                    cylinder (h=100, r=50, center=true);
                  }
                }
              }
            }
          }
          translate ([0, 0, -80]) {
            difference () {
              cylinder (h=30, r=96, center=true);
              cylinder (h=31, r=92, center=true);
              translate ([0, 0, 15.1]) {
                cylinder (h=30, r1=92, r2=100, center=true);
              }
            }
          }
        }
      }
    }
  }
}

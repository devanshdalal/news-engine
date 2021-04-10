import { ActionTypes } from "../constants/ActionTypes";

const initialState = {
  items: [],
  itemsApiInProgress: false,
  itemsApiInError: "",
  totalItemCount: 1,
  filters: {
    search: "",
    limit: 30,
    skip: 0
  }
};

const itemsReducer = (state = initialState, action) => {
  switch (action.type) {
    case ActionTypes.GET_ITEMS:
      return Object.assign({}, state, {
        itemsApiInProgress: true,
        itemsApiInError: ""
      });
    case ActionTypes.GET_ITEMS_FAILURE:
      return Object.assign({}, state, {
        itemsApiInProgress: false,
        itemsApiInError: action.payload
      });
    case ActionTypes.GET_ITEMS_SUCCESS:
      return Object.assign({}, state, {
        items: [...state.items, ...action.payload],
        totalItemCount: state.items.length + action.payload.length,
        itemsApiInProgress: false,
        filters: {
          ...state.filters,
          skip: state.filters.skip + state.filters.limit
        }
      });
    case ActionTypes.CLEAR_ITEMS_LIST:
      return Object.assign({}, state, {
        items: []
      });
    case ActionTypes.RESET_STORE:
      return {
        ...initialState,
        filters: {
          ...initialState.filters,
          ...{ search: state.filters.search }
        }
      };
    case ActionTypes.ITEMS_FILTER_CHANGE: {
      return Object.assign({}, state, {
        filters: { ...state.filters, ...action.payload }
      });
    }
    default:
      return state;
  }
};

export default itemsReducer;

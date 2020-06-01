import React, { Component } from 'react';
import { Form, FormGroup, Input, Label, Button, ButtonGroup, Container, Table, Badge, Alert, Collapse } from 'reactstrap';
import { Link, withRouter } from 'react-router-dom';
import { instanceOf } from 'prop-types';
import { withCookies, Cookies } from 'react-cookie';
import AppNavbar from './NavBar';

class PatientList extends Component {
  static propTypes = {
    cookies: instanceOf(Cookies).isRequired
  };

  constructor(props) {
    super(props);
    const { cookies } = props;
    this.state = { patients: [], errors: [], csrfToken: cookies.get('XSRF-TOKEN'), isLoading: true, isSearchOpen: false, searchCondition: { withDeleted: false } };
    this.remove = this.remove.bind(this);
  }

  componentDidMount() {
    this.setState({ isLoading: true });

    fetch('patients', { credentials: 'include' })
      .then(response => response.json())
      .then(data => this.setState({ patients: data, isLoading: false }))
      .catch(() => this.props.history.push('/'));
  }

  async remove(id) {
    await fetch(`/patients/${id}`, {
      method: 'DELETE',
      headers: {
        'X-XSRF-TOKEN': this.state.csrfToken,
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      credentials: 'include'
    }).then(() => {
      let updatedPatients = [...this.state.patients].filter(i => i.id !== id);
      this.setState({ patients: updatedPatients });
    });
  }

  handleChange(event) {
    const target = event.target;
    const value = target.value;
    const name = target.name;
    let searchCondition = { ...this.state.searchCondition };
    searchCondition[name] = value;
    this.setState({ searchCondition: searchCondition });
  } toggleWithDeleted

  toggleWithDeleted(event) {
    let searchCondition = { ...this.state.searchCondition };
    searchCondition['withDeleted'] = !searchCondition['withDeleted'];
    this.setState({ searchCondition: searchCondition });
  }

  
  toggleIsSearchOpen(event) {
    console.log('toggle')
    const { isSearchOpen } = this.state;
    this.setState({ isSearchOpen: !isSearchOpen });
  }


  async handleSearch(event) {
    // event.preventDefault();
    this.setState({ isLoading: true });

    const { searchCondition, isSearchOpen } = this.state
    let query = '?'
    for (let [key, value] of Object.entries(searchCondition)) {
      query = query.concat(`${key}=${value}&`)
    }
    const patients = await (await fetch(`/patients${query}`)).json();

    if (patients.status && patients.status >= 400 && patients.status <= 500) {
      this.setState({ errors: patients.messages})
    } else {
      this.setState({ patients})
    }

    this.setState({ isSearchOpen: !isSearchOpen , isLoading: false  });

  }

  render() {
    const { patients, isLoading, errors, searchCondition, isSearchOpen } = this.state;

    if (isLoading) {
      return <p>Loading...</p>;
    }

    const patientList = patients.map(patient => {
      const fullName = `${patient.firstName || ''} ${patient.middleName || ''} ${patient.lastName || ''}`;
      return <tr key={patient.id}>
        <td style={{ whiteSpace: 'nowrap' }}>{patient.patientId}</td>
        <td>{fullName}</td>
        <td>{patient.gender === 'M' ? 'Male' : patient.gender === 'F' ? 'Female' : 'Other'}</td>
        <td>{patient.dob}</td>
        <td>
          {patient.deleteFlag ?
            <Badge color='danger'>Deleted</Badge>
            :
            <ButtonGroup>
              <Button size="sm" color="primary" tag={Link} to={"/patients/" + patient.id}>Edit</Button>
              <Button size="sm" color="danger" onClick={() => this.remove(patient.id)}>Delete</Button>
            </ButtonGroup>
          }
        </td>
      </tr>
    });

    const searchForm = <Form onSubmit={this.handleSearch.bind(this)}>
      {errors.length ?
        <div>
          {errors.map(error => {
            return <Alert color="danger">
              {error}
            </Alert>;
          })}

        </div>
        :
        null}
      <FormGroup check>
        <Label for="withDeleted" check>
          <Input type="checkbox" checked={searchCondition.withDeleted ? 'checked' : ''}  name="withDeleted" id="withDeleted" onChange={this.toggleWithDeleted.bind(this)} />{' '}
          With Deleted
        </Label>
      </FormGroup>
      <FormGroup>
        <Label for="PatientID">Patient ID</Label>
        <Input type="text" name="PatientID" id="PatientID" value={searchCondition.PatientID || ''}
          onChange={this.handleChange.bind(this)} autoComplete="PatientID" />
      </FormGroup>
      <FormGroup>
        <Label for="FirstName">First Name</Label>
        <Input type="text" name="FirstName" id="FirstName" value={searchCondition.FirstName || ''}
          onChange={this.handleChange.bind(this)} autoComplete="FirstName" />
      </FormGroup>
      <FormGroup>
        <Label for="LastName">Last Name</Label>
        <Input type="text" name="LastName" id="LastName" value={searchCondition.LastName || ''}
          onChange={this.handleChange.bind(this)} autoComplete="LastName" />
      </FormGroup>

      <FormGroup>
        <Label for="gender">Gender</Label>
        <Input type="select" name="gender" value={searchCondition.gender} id="gender" onChange={this.handleChange.bind(this)}>
          <option value="M" defaultChecked>Male</option>
          <option value="F">Female</option>
          <option value="O">Other</option>
          <option value="">None</option>
        </Input>
      </FormGroup>
      <FormGroup>
        <Label for="dob">Date Of Birth (not available)</Label>
        <Input
          type="date"
          name="dob"
          id="dob"
          placeholder=""
          onChange={this.handleChange.bind(this)}
          value={searchCondition.dob}
          readOnly
        />
      </FormGroup>
      <FormGroup>
        <Button>Search</Button>
      </FormGroup>
    </Form>;


    return (
      <div>
        <AppNavbar />
        <Container fluid>
          <div className="float-right">
            <Button color="success" tag={Link} to="/patients/new">Add Patient</Button>
          </div>
          <div>
            <Button color="primary" onClick={this.toggleIsSearchOpen.bind(this)} style={{ marginBottom: '1rem' }}>Toggle</Button>
            <Collapse isOpen={isSearchOpen}>
              {searchForm}
            </Collapse>
          </div>

          <h3>Patients Management</h3>
          <Table hover className="mt-4">
            <thead>
              <tr>
                <th width="10%">Patient ID</th>
                <th width="30%">Patient Name</th>
                <th width="20%">Gender</th>
                <th width="30%">DOB</th>
                <th width="10%">Actions</th>
              </tr>
            </thead>
            <tbody>
              {patientList}
            </tbody>
          </Table>
        </Container>
      </div>
    );
  }
}

export default withCookies(withRouter(PatientList));